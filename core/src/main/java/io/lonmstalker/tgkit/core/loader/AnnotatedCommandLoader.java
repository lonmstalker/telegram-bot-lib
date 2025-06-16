package io.lonmstalker.tgkit.core.loader;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotHandlerConverter;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.CustomMatcher;
import io.lonmstalker.tgkit.core.annotation.MessageContainsMatch;
import io.lonmstalker.tgkit.core.annotation.MessageRegexMatch;
import io.lonmstalker.tgkit.core.annotation.MessageTextMatch;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.annotation.UserRoleMatch;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistry;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import io.lonmstalker.tgkit.core.args.BotArgumentConverter;
import io.lonmstalker.tgkit.core.args.Converters;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Objects;
import java.util.Set;

import io.lonmstalker.tgkit.core.matching.AlwaysMatch;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

/** Utility to scan packages for {@link BotHandler} methods. */
@UtilityClass
public final class AnnotatedCommandLoader {

    /**
     * Сканирует указанные пакеты и регистрирует все методы, помеченные
     * аннотацией {@link BotHandler}.
     */
    @SuppressWarnings({"argument"})
    public static void load(@NonNull BotCommandRegistry registry,
                            @NonNull String... packages) {
        ConfigurationBuilder cb = new ConfigurationBuilder();

        cb.forPackages(packages);
        cb.addScanners(Scanners.MethodsAnnotated);
        Reflections reflections = new Reflections(cb);

        Set<Method> methods = reflections.getMethodsAnnotatedWith(BotHandler.class);
        for (Method method : methods) {
            registerHandler(registry, method);
        }
    }

    /** Обрабатывает один найденный метод-хендлер и регистрирует его. */
    @SuppressWarnings({"argument"})
    private static void registerHandler(@NonNull BotCommandRegistry registry, @NonNull Method method) {
        if (Modifier.isStatic(method.getModifiers())) {
            throw new BotApiException("Handler methods must not be static: " + method);
        }

        BotHandler ann = Objects.requireNonNull(method.getAnnotation(BotHandler.class));
        Object instance = createInstance(method.getDeclaringClass());
        CommandMatch<? extends BotApiObject> matcher = extractMatcher(method);
        BotHandlerConverter<?> converter = (BotHandlerConverter<?>) createInstance(ann.converter());

        method.setAccessible(true);
        BotCommand<BotApiObject> cmd = InternalCommandAdapter.builder()
                .method(method)
                .type(ann.type())
                .order(ann.order())
                .instance(instance)
                .converter(converter)
                .commandMatch(matcher)
                .botGroup(ann.botGroup())
                .params(extractParameters(method))
                .build();
        registry.add(cmd);
    }

    /**
     * Создаёт экземпляр указанного класса, учитывая возможный метод getInstance().
     */
    @SuppressWarnings("return")
    private static @NonNull Object createInstance(@NonNull Class<?> clazz) {
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

    /**
     * Создаёт объект сравнения, исходя из аннотаций на методе.
     */
    @SuppressWarnings({"unchecked", "argument"})
    private static @NonNull CommandMatch<? extends BotApiObject> extractMatcher(@NonNull Method method) {
        if (method.isAnnotationPresent(MessageContainsMatch.class)) {
            MessageContainsMatch mc = method.getAnnotation(MessageContainsMatch.class);
            return new io.lonmstalker.tgkit.core.matching.MessageContainsMatch(Objects.requireNonNull(mc).value(), mc.ignoreCase());
        } else if (method.isAnnotationPresent(MessageRegexMatch.class)) {
            MessageRegexMatch mr = method.getAnnotation(MessageRegexMatch.class);
            return new io.lonmstalker.tgkit.core.matching.MessageRegexMatch(Objects.requireNonNull(mr).value());
        } else if (method.isAnnotationPresent(MessageTextMatch.class)) {
            MessageTextMatch mt = method.getAnnotation(MessageTextMatch.class);
            return new io.lonmstalker.tgkit.core.matching.MessageTextMatch(Objects.requireNonNull(mt).value(), mt.ignoreCase());
        } else if (method.isAnnotationPresent(UserRoleMatch.class)) {
            UserRoleMatch ur = method.getAnnotation(UserRoleMatch.class);
            var provider = (BotUserProvider) createInstance(Objects.requireNonNull(ur).provider());
            return new io.lonmstalker.tgkit.core.matching.UserRoleMatch<>(provider, Set.of(ur.roles()));
        } else {
            CustomMatcher custom = method.getAnnotation(CustomMatcher.class);
            if (custom != null) {
                return  (CommandMatch<BotApiObject>) createInstance(custom.value());
            }
        }
        return new AlwaysMatch<>();
    }

    /**
     * Формирует информацию о параметрах метода для последующего вызова хендлера.
     */
    private InternalCommandAdapter.ParamInfo[] extractParameters(@NonNull Method method) {
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

        return infos;
    }
}
