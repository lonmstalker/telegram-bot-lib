package io.lonmstalker.core.annotation;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.bot.BotCommandRegistry;
import io.lonmstalker.core.matching.AndMatch;
import io.lonmstalker.core.matching.CommandMatch;
import io.lonmstalker.core.matching.MessageContainsMatch;
import io.lonmstalker.core.matching.MessageRegexMatch;
import io.lonmstalker.core.matching.MessageTextMatch;
import io.lonmstalker.core.matching.UserRoleMatch;
import io.lonmstalker.core.user.BotUserProvider;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import lombok.NonNull;
import io.lonmstalker.core.exception.BotApiException;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import io.lonmstalker.core.annotation.EnumValueConverter;
import io.lonmstalker.core.annotation.AnnotatedBotCommand;

/**
 * Loader that registers commands defined via annotations.
 */
public class AnnotationCommandLoader {
    private final BotCommandRegistry registry;
    private final BotUserProvider userProvider;

    public AnnotationCommandLoader(@NonNull BotCommandRegistry registry,
                                   @NonNull BotUserProvider userProvider) {
        this.registry = registry;
        this.userProvider = userProvider;
    }

    /**
     * Scan and register annotated methods of the given handler instance.
     */
    public void load(@NonNull Object handler) {
        for (Method method : handler.getClass().getDeclaredMethods()) {
            BotCommandHandler ann = method.getAnnotation(BotCommandHandler.class);
            if (ann == null) {
                continue;
            }
            method.setAccessible(true);
            registry.add(createCommand(handler, method, ann));
        }
    }

    private BotCommand<BotApiObject> createCommand(Object handler, Method method, BotCommandHandler ann) {
        CommandMatch<BotApiObject> matcher = buildMatcher(method, ann);
        BotRequestConverter<?> converter = determineConverter(method, ann);
        BotRequestType type = ann.type();
        int order = ann.order();
        return AnnotatedBotCommand.builder()
                .handler(handler)
                .method(method)
                .converter(converter)
                .matcher(matcher)
                .type(type)
                .order(order)
                .userProvider(userProvider)
                .build();
    }

    private CommandMatch<BotApiObject> buildMatcher(Method method, BotCommandHandler ann) {
        List<CommandMatch<BotApiObject>> matches = new ArrayList<>();
        TextMatch text = method.getAnnotation(TextMatch.class);
        if (text != null) {
            matches.add((CommandMatch) new MessageTextMatch(text.value(), text.ignoreCase()));
        }
        RegexMatch regex = method.getAnnotation(RegexMatch.class);
        if (regex != null) {
            matches.add((CommandMatch) new MessageRegexMatch(regex.value()));
        }
        ContainsMatch contains = method.getAnnotation(ContainsMatch.class);
        if (contains != null) {
            matches.add((CommandMatch) new MessageContainsMatch(contains.value(), contains.ignoreCase()));
        }
        UserRoleMatch roles = method.getAnnotation(UserRoleMatch.class);
        if (roles != null) {
            matches.add((CommandMatch) new UserRoleMatch(userProvider, Set.of(roles.value())));
        }
        for (Class<? extends CommandMatch<?>> cls : ann.customMatchers()) {
            matches.add(instantiateMatcher(cls));
        }
        if (matches.isEmpty()) {
            throw new BotApiException("No matchers defined for method " + method.getName());
        }
        return new AndMatch<>(matches);
    }

    private CommandMatch<BotApiObject> instantiateMatcher(Class<? extends CommandMatch<?>> cls) {
        try {
            return (CommandMatch<BotApiObject>) cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BotApiException("Cannot instantiate matcher: " + cls, e);
        }
    }

    private BotRequestConverter<?> determineConverter(Method method, BotCommandHandler ann) {
        Class<? extends BotRequestConverter<?>> cls = ann.converter();
        if (cls == BotRequestConverterImpl.class) {
            Class<?> dataClass = extractDataClass(method);
            if (dataClass.isEnum()) {
                return new EnumValueConverter(dataClass.asSubclass(Enum.class));
            }
            return new BotRequestConverterImpl();
        }
        return instantiateConverter(cls);
    }

    private BotRequestConverter<?> instantiateConverter(Class<? extends BotRequestConverter<?>> cls) {
        try {
            return cls.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new BotApiException("Cannot instantiate converter: " + cls, e);
        }
    }

    private Class<?> extractDataClass(Method method) {
        if (method.getParameterCount() == 0) {
            return BotApiObject.class;
        }
        Type type = method.getGenericParameterTypes()[0];
        if (type instanceof ParameterizedType pt) {
            Type arg = pt.getActualTypeArguments()[0];
            if (arg instanceof Class<?> c) {
                return c;
            }
        }
        return BotApiObject.class;
    }
}
