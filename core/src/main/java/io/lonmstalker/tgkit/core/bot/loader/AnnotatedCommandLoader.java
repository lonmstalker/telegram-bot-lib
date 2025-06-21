/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.core.bot.loader;

import static io.github.tgkit.core.reflection.ReflectionUtils.newInstance;

import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.BotHandlerConverter;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.annotation.Arg;
import io.github.tgkit.core.annotation.BotHandler;
import io.github.tgkit.core.annotation.matching.CustomMatcher;
import io.github.tgkit.core.annotation.matching.MessageContainsMatch;
import io.github.tgkit.core.annotation.matching.MessageRegexMatch;
import io.github.tgkit.core.annotation.matching.MessageTextMatch;
import io.github.tgkit.core.annotation.matching.UserRoleMatch;
import io.github.tgkit.core.args.BotArgumentConverter;
import io.github.tgkit.core.args.Converters;
import io.github.tgkit.core.args.ParamInfo;
import io.github.tgkit.core.bot.BotCommandRegistry;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.event.impl.RegisterCommandBotEvent;
import io.github.tgkit.core.exception.BotApiException;
import io.github.tgkit.core.loader.BotCommandFactory;
import io.github.tgkit.core.matching.AlwaysMatch;
import io.github.tgkit.core.matching.CommandMatch;
import io.github.tgkit.core.user.BotUserProvider;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.time.Instant;
import java.util.Objects;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArrayList;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Utility to scan packages for {@link BotHandler} methods. */
public final class AnnotatedCommandLoader {
  private static final Logger log = LoggerFactory.getLogger(AnnotatedCommandLoader.class);

  private AnnotatedCommandLoader() {}

  private static final List<BotCommandFactory<?>> FACTORIES = new CopyOnWriteArrayList<>();

  static {
    ServiceLoader.load(BotCommandFactory.class).forEach(FACTORIES::add);
    log.debug("loaded {} BotCommandFactory into FACTORIES", FACTORIES.size());
  }

  public static void addCommandFactory(@NonNull BotCommandFactory<?> factory) {
    FACTORIES.add(factory);
    log.debug("added {} inti FACTORIES", factory.getClass().getSimpleName());
  }

  /**
   * Сканирует указанные пакеты и регистрирует все методы, помеченные аннотацией {@link BotHandler}.
   */
  @SuppressWarnings({"argument"})
  public static void load(@NonNull BotCommandRegistry registry, @NonNull String... packages) {
    ConfigurationBuilder cb = new ConfigurationBuilder();
    log.debug("[command-load] loading commands in package {} ", packages.length);

    cb.forPackages(packages);
    cb.addScanners(Scanners.MethodsAnnotated);
    Reflections reflections = new Reflections(cb);

    Set<Method> methods = reflections.getMethodsAnnotatedWith(BotHandler.class);
    for (Method method : methods) {
      registerHandler(registry, method);
    }

    log.debug("[command-load] loaded {} commands in package {}", methods.size(), packages);
  }

  /** Обрабатывает один найденный метод-хендлер и регистрирует его. */
  @SuppressWarnings({"argument", "unchecked"})
  private static void registerHandler(
      @NonNull BotCommandRegistry registry, @NonNull Method method) {
    if (Modifier.isStatic(method.getModifiers())) {
      throw new BotApiException("Handler methods must not be static: " + method);
    }

    BotHandler ann = Objects.requireNonNull(method.getAnnotation(BotHandler.class));
    Object instance = newInstance(method.getDeclaringClass());
    CommandMatch<? extends BotApiObject> matcher = extractMatcher(method);
    BotHandlerConverter<Object> converter =
        (BotHandlerConverter<Object>) newInstance(ann.converter());

    method.setAccessible(true);
    BotCommand<BotApiObject> cmd =
        InternalCommandAdapter.builder()
            .method(method)
            .type(ann.type())
            .order(ann.order())
            .instance(instance)
            .converter(converter)
            .commandMatch(matcher)
            .botGroup(ann.botGroup())
            .params(extractParameters(method))
            .build();

    applyFactories(cmd, method);
    BotGlobalConfig.INSTANCE
        .events()
        .getBus()
        .publish(new RegisterCommandBotEvent(Instant.now(), method, cmd));
    registry.add(cmd);
  }

  /** Создаёт объект сравнения, исходя из аннотаций на методе. */
  @SuppressWarnings({"unchecked", "argument"})
  private static @NonNull CommandMatch<? extends BotApiObject> extractMatcher(
      @NonNull Method method) {
    if (method.isAnnotationPresent(MessageContainsMatch.class)) {
      MessageContainsMatch mc = method.getAnnotation(MessageContainsMatch.class);
      return new io.github.tgkit.core.matching.MessageContainsMatch(
          Objects.requireNonNull(mc).value(), mc.ignoreCase());
    } else if (method.isAnnotationPresent(MessageRegexMatch.class)) {
      MessageRegexMatch mr = method.getAnnotation(MessageRegexMatch.class);
      return new io.github.tgkit.core.matching.MessageRegexMatch(
          Objects.requireNonNull(mr).value());
    } else if (method.isAnnotationPresent(MessageTextMatch.class)) {
      MessageTextMatch mt = method.getAnnotation(MessageTextMatch.class);
      return new io.github.tgkit.core.matching.MessageTextMatch(
          Objects.requireNonNull(mt).value(), mt.ignoreCase());
    } else if (method.isAnnotationPresent(UserRoleMatch.class)) {
      UserRoleMatch ur = method.getAnnotation(UserRoleMatch.class);
      var provider = (BotUserProvider) newInstance(Objects.requireNonNull(ur).provider());
      return new io.github.tgkit.core.matching.UserRoleMatch<>(provider, Set.of(ur.roles()));
    } else {
      CustomMatcher custom = method.getAnnotation(CustomMatcher.class);
      if (custom != null) {
        return (CommandMatch<BotApiObject>) newInstance(custom.value());
      }
    }
    return new AlwaysMatch<>();
  }

  /** Формирует информацию о параметрах метода для последующего вызова хендлера. */
  @SuppressWarnings({"unchecked", "rawtypes"})
  private @NonNull ParamInfo[] extractParameters(@NonNull Method m) {
    var ps = m.getParameters();
    var anns = m.getParameterAnnotations();
    ParamInfo[] out = new ParamInfo[ps.length];

    for (int i = 0; i < ps.length; i++) {
      Parameter p = ps[i];
      Class<?> pt = p.getType();

      // --- BotRequest<T> ---
      if (BotRequest.class.isAssignableFrom(pt)) {
        out[i] =
            new ParamInfo(
                i,
                true,
                false,
                null,
                p,
                Converters.getByClass((Class) BotArgumentConverter.RequestConverter.class));
        continue;
      }

      // --- Update ---
      if (Update.class.equals(pt)) {
        out[i] =
            new ParamInfo(
                i,
                false,
                true,
                null,
                p,
                Converters.getByClass((Class) BotArgumentConverter.UpdateConverter.class));
        continue;
      }

      // --- @Arg ---
      Arg arg = null;
      for (Annotation a : anns[i]) {
        if (a instanceof Arg) {
          arg = (Arg) a;
        }
      }
      if (arg == null) {
        throw new BotApiException("Параметр без @Arg должен быть BotRequest или Update: " + m);
      }

      BotArgumentConverter conv =
          arg.converter() == BotArgumentConverter.UpdateConverter.class
              ? Converters.getByType(pt)
              : Converters.getByClass((Class) arg.converter());

      out[i] = new ParamInfo(i, false, false, arg, p, conv);
    }
    return out;
  }

  @SuppressWarnings({"unchecked", "rawtypes", "argument"})
  private static void applyFactories(@NonNull BotCommand<?> command, @NonNull Method m) {
    for (BotCommandFactory f : FACTORIES) {
      if (f.annotationType() == BotCommandFactory.None.class) {
        f.apply(command, m, null);
      } else {
        f.apply(command, m, m.getAnnotation(Objects.requireNonNull(f.annotationType())));
      }
    }
  }
}
