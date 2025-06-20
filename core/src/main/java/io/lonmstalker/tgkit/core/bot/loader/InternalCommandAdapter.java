package io.lonmstalker.tgkit.core.bot.loader;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotHandlerConverter;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.annotation.Arg;
import io.lonmstalker.tgkit.core.args.Context;
import io.lonmstalker.tgkit.core.args.ParamInfo;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import io.lonmstalker.tgkit.core.storage.BotRequestContextHolder;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Адаптер одного метода-хендлера, извлекающий аргументы, конвертирующий запрос и вызывающий
 * рефлективно целевой метод.
 */
class InternalCommandAdapter implements BotCommand<BotApiObject> {

  /** Порядок выполнения команды. */
  private final int order;

  /** Метаданные параметров метода. */
  private final ParamInfo[] params;

  /** Сам метод-хендлер. */
  private final @NonNull Method method;

  /** Инстанс класса, содержащего метод. */
  private final @NonNull Object instance;

  /** Группа команд (botGroup). */
  private volatile @NonNull String botGroup;

  /** Тип запроса (MESSAGE, CALLBACK_QUERY и т.п.). */
  private volatile @NonNull BotRequestType type;

  /** Конвертер BotRequest → нужный для метода тип. */
  private volatile @NonNull BotHandlerConverter<Object> converter;

  /** Правило матчинга команды. */
  private volatile @NonNull CommandMatch<? extends BotApiObject> commandMatch;

  /** Интерсепторы до/после вызова метода. */
  private final @NonNull List<BotInterceptor> interceptors = new CopyOnWriteArrayList<>();

  static Builder builder() {
    return new Builder();
  }

  InternalCommandAdapter(
      int order,
      ParamInfo[] params,
      @NonNull Method method,
      @NonNull Object instance,
      @NonNull String botGroup,
      @NonNull BotRequestType type,
      @NonNull BotHandlerConverter<Object> converter,
      @NonNull CommandMatch<? extends BotApiObject> commandMatch) {
    this.order = order;
    this.params = params;
    this.method = method;
    this.instance = instance;
    this.botGroup = botGroup;
    this.type = type;
    this.converter = converter;
    this.commandMatch = commandMatch;
  }

  static class Builder {
    private int order;
    private ParamInfo[] params;
    private Method method;
    private Object instance;
    private String botGroup;
    private BotRequestType type;
    private BotHandlerConverter<Object> converter;
    private CommandMatch<? extends BotApiObject> commandMatch;

    Builder order(int order) {
      this.order = order;
      return this;
    }

    Builder params(ParamInfo[] params) {
      this.params = params;
      return this;
    }

    Builder method(@NonNull Method method) {
      this.method = method;
      return this;
    }

    Builder instance(@NonNull Object instance) {
      this.instance = instance;
      return this;
    }

    Builder botGroup(@NonNull String botGroup) {
      this.botGroup = botGroup;
      return this;
    }

    Builder type(@NonNull BotRequestType type) {
      this.type = type;
      return this;
    }

    Builder converter(@NonNull BotHandlerConverter<Object> converter) {
      this.converter = converter;
      return this;
    }

    Builder commandMatch(@NonNull CommandMatch<? extends BotApiObject> match) {
      this.commandMatch = match;
      return this;
    }

    InternalCommandAdapter build() {
      return new InternalCommandAdapter(
          order, params, method, instance, botGroup, type, converter, commandMatch);
    }
  }

  @Override
  @SuppressWarnings("argument")
  public @Nullable BotResponse handle(@NonNull BotRequest<BotApiObject> request) {
    Update update = BotRequestContextHolder.getUpdateNotNull();

    // preHandle
    for (BotInterceptor i : interceptors) {
      i.preHandle(update, request);
    }

    Object converted = converter.convert(request);
    Object[] args = new Object[params.length];
    for (int i = 0; i < params.length; i++) {
      ParamInfo pi = params[i];
      if (pi.request()) {
        args[i] = converted;
      } else if (pi.update()) {
        args[i] = update;
      } else {
        // Arg-параметр
        String raw = null;
        Arg arg = Objects.requireNonNull(pi.arg());
        var matcher = RouteContextHolder.getMatcher();
        if (matcher != null) {
          try {
            raw = matcher.group(arg.value());
          } catch (IllegalArgumentException ignored) {
          }
        }
        if (StringUtils.isEmpty(raw) && !arg.required()) {
          raw = arg.defaultValue();
        }
        if (raw == null || raw.isEmpty()) {
          throw new BotApiException("Required arg missing: " + arg.value());
        }
        Context<Object> ctx = new Context<>(request, matcher);
        args[i] = Objects.requireNonNull(pi.converter().convert(raw, ctx));
      }
    }

    BotResponse response;
    try {
      Object res = method.invoke(instance, args);
      if (res == null) {
        triggerAfterCompletion(update, request, null, null);
        return null;
      }
      if (!(res instanceof BotResponse)) {
        throw new BotApiException("Handler must return BotResponse");
      }
      response = (BotResponse) res;
      triggerAfterCompletion(update, request, response, null);
      return response;
    } catch (Exception e) {
      triggerAfterCompletion(update, request, null, e);
      throw new BotApiException("Handler invocation error", e);
    }
  }

  private void triggerAfterCompletion(
      @NonNull Update update,
      @NonNull BotRequest<BotApiObject> request,
      @Nullable BotResponse response,
      @Nullable Exception exception) {
    for (BotInterceptor i : interceptors) {
      try {
        i.afterCompletion(update, request, response, exception);
      } catch (Exception ignored) {
      }
    }
  }

  @Override
  public @NonNull BotRequestType type() {
    return type;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull CommandMatch<BotApiObject> matcher() {
    return (CommandMatch<BotApiObject>) commandMatch;
  }

  @Override
  public @NonNull List<BotInterceptor> interceptors() {
    return List.copyOf(interceptors);
  }

  @Override
  public void setMatcher(@NonNull CommandMatch<BotApiObject> matcher) {
    this.commandMatch = matcher;
  }

  @Override
  public void setType(@NonNull BotRequestType type) {
    this.type = type;
  }

  @Override
  public void setBotGroup(@NonNull String group) {
    this.botGroup = group;
  }

  @Override
  public void addInterceptor(@NonNull BotInterceptor interceptor) {
    interceptors.add(interceptor);
  }

  @Override
  public @NonNull String botGroup() {
    return botGroup;
  }

  @Override
  public int order() {
    return order;
  }
}
