package io.lonmstalker.core.loader;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotHandlerConverter;
import io.lonmstalker.core.annotation.BotHandler;
import io.lonmstalker.core.annotation.CustomMatcher;
import io.lonmstalker.core.annotation.MessageContainsMatch;
import io.lonmstalker.core.annotation.MessageRegexMatch;
import io.lonmstalker.core.annotation.MessageTextMatch;
import io.lonmstalker.core.annotation.Arg;
import io.lonmstalker.core.annotation.UserRoleMatch;
import io.lonmstalker.core.bot.BotCommandRegistry;
import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.matching.CommandMatch;
import io.lonmstalker.core.args.BotArgumentConverter;
import io.lonmstalker.core.args.Converters;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;

import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

/** Utility to scan packages for {@link BotHandler} methods. */
@UtilityClass
public final class AnnotatedCommandLoader {

    @SuppressWarnings({"unchecked", "argument"})
    public static void load(@NonNull BotCommandRegistry registry,
                            @NonNull String... packages) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.forPackages(packages);
        cb.addScanners(Scanners.MethodsAnnotated);
        Reflections reflections = new Reflections(cb);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(BotHandler.class);
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new BotApiException("Handler methods must not be static: " + method);
            }

            BotHandler ann = Objects.requireNonNull(method.getAnnotation(BotHandler.class));
            Object instance = createInstance(method.getDeclaringClass());

            CommandMatch<? extends BotApiObject> matcher = null;
            if (method.isAnnotationPresent(MessageContainsMatch.class)) {
                MessageContainsMatch mc = method.getAnnotation(MessageContainsMatch.class);
                matcher = new io.lonmstalker.core.matching.MessageContainsMatch(mc.value(), mc.ignoreCase());
            } else if (method.isAnnotationPresent(MessageRegexMatch.class)) {
                MessageRegexMatch mr = method.getAnnotation(MessageRegexMatch.class);
                matcher = new io.lonmstalker.core.matching.MessageRegexMatch(mr.value());
            } else if (method.isAnnotationPresent(MessageTextMatch.class)) {
                MessageTextMatch mt = method.getAnnotation(MessageTextMatch.class);
                matcher = new io.lonmstalker.core.matching.MessageTextMatch(mt.value(), mt.ignoreCase());
            } else if (method.isAnnotationPresent(UserRoleMatch.class)) {
                UserRoleMatch ur = method.getAnnotation(UserRoleMatch.class);
                var provider = (io.lonmstalker.core.user.BotUserProvider) createInstance(ur.provider());
                matcher = new io.lonmstalker.core.matching.UserRoleMatch<>(provider, Set.of(ur.roles()));
            } else {
                CustomMatcher custom = method.getAnnotation(CustomMatcher.class);
                if (custom != null) {
                    matcher = (CommandMatch<BotApiObject>) createInstance(custom.value());
                }
            }
            if (matcher == null) {
                matcher = new io.lonmstalker.core.matching.AlwaysMatch<>();
            }

            BotHandlerConverter<?> converter = (BotHandlerConverter<?>) createInstance(ann.converter());

            method.setAccessible(true);
            var parameters = method.getParameters();
            InternalCommandAdapter.ParamInfo[] infos = new InternalCommandAdapter.ParamInfo[parameters.length];
            for (int i = 0; i < parameters.length; i++) {
                var p = parameters[i];
                Arg a = p.getAnnotation(Arg.class);
                if (a != null) {
                    BotArgumentConverter<?> conv;
                    if (a.converter() != BotArgumentConverter.Identity.class) {
                        conv = Converters.getByClass(a.converter());
                    } else {
                        conv = Converters.getByType(p.getType());
                    }
                    infos[i] = new InternalCommandAdapter.ParamInfo(false, a, conv);
                } else {
                    infos[i] = new InternalCommandAdapter.ParamInfo(true, null, null);
                }
            }
            BotCommand<BotApiObject> cmd = InternalCommandAdapter.builder()
                    .method(method)
                    .type(ann.type())
                    .order(ann.order())
                    .instance(instance)
                    .converter(converter)
                    .commandMatch(matcher)
                    .params(infos)
                    .botGroup(ann.botGroup())
                    .build();
            registry.add(cmd);
        }
    }

    private static Object createInstance(Class<?> clazz) {
        try {
            Method getInstance = clazz.getDeclaredMethod("getInstance");
            if (Modifier.isStatic(getInstance.getModifiers())) {
                getInstance.setAccessible(true);
                return getInstance.invoke(null);
            }
        } catch (NoSuchMethodException ignored) {
            // no getInstance method
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new BotApiException("Cannot invoke getInstance for " + clazz.getName(), e);
        }

        try {
            Constructor<?> ctor = clazz.getDeclaredConstructor();
            ctor.setAccessible(true);
            return ctor.newInstance();
        } catch (NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
            throw new BotApiException("Cannot instantiate " + clazz.getName(), e);
        }
    }
}
