package io.lonmstalker.core.loader;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.BotHandlerConverter;
import io.lonmstalker.core.annotation.BotHandler;
import io.lonmstalker.core.bot.BotCommandRegistry;
import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.matching.CommandMatch;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
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

    @SuppressWarnings("unchecked")
    public static void load(@NonNull BotCommandRegistry registry, @NonNull String... packages) {
        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.forPackages(packages);
        cb.addScanners(Scanners.MethodsAnnotated);
        Reflections reflections = new Reflections(cb);
        Set<Method> methods = reflections.getMethodsAnnotatedWith(BotHandler.class);
        for (Method method : methods) {
            if (Modifier.isStatic(method.getModifiers())) {
                throw new BotApiException("Handler methods must not be static: " + method);
            }
            BotHandler ann = method.getAnnotation(BotHandler.class);

            Object instance = createInstance(method.getDeclaringClass());

            CommandMatch<BotApiObject> matcher = (CommandMatch<BotApiObject>) createInstance(ann.matcher());

            BotHandlerConverter<?> converter = (BotHandlerConverter<?>) createInstance(ann.converter());

            method.setAccessible(true);
            BotCommand<BotApiObject> cmd = new BotCommand<>() {
                @Override
                public BotResponse handle(@NonNull BotRequest<BotApiObject> request) {
                    try {
                        Object arg = converter.convert(request);
                        Object res = method.invoke(instance, arg);
                        if (res == null) {
                            return null;
                        }
                        if (res instanceof BotResponse r) {
                            return r;
                        }
                        throw new BotApiException("Handler must return BotResponse");
                    } catch (IllegalAccessException | InvocationTargetException e) {
                        throw new BotApiException("Handler invocation error", e);
                    }
                }

                @Override
                public @NonNull BotRequestType type() {
                    return ann.type();
                }

                @Override
                public @NonNull CommandMatch<BotApiObject> matcher() {
                    return matcher;
                }

                @Override
                public int order() {
                    return ann.order();
                }
            };
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
