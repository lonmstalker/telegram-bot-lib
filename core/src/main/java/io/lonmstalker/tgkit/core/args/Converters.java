package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.reflection.ReflectionUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Утилитарный реестр, где хранятся все кастомные BotArgumentConverter. */
public final class Converters {
  private Converters() {}
  private static final Map<Class<?>, BotArgumentConverter<?, ?>> BY_TYPE =
      new ConcurrentHashMap<>();
  private static final Map<Class<?>, BotArgumentConverter<?, ?>> BY_CLASS =
      new ConcurrentHashMap<>();

  static {
    register(Update.class, new BotArgumentConverter.UpdateConverter());
    register(BotRequest.class, new BotArgumentConverter.RequestConverter());
    // ... добавляем Number/Boolean/Enum и т.п.
  }

  public static <T> void register(
      @NonNull Class<T> type, @NonNull BotArgumentConverter<?, ?> converter) {
    BY_TYPE.put(type, converter);
    BY_CLASS.put(converter.getClass(), converter);
  }

  @SuppressWarnings("unchecked")
  public static <T> BotArgumentConverter<Object, T> getByType(@NonNull Class<T> type) {
    return (BotArgumentConverter<Object, T>) BY_TYPE.get(type);
  }

  @SuppressWarnings("unchecked")
  public static <T> BotArgumentConverter<Object, T> getByClass(
      @NonNull Class<? extends BotArgumentConverter<Object, Object>> cls) {
    return (BotArgumentConverter<Object, T>)
        BY_CLASS.computeIfAbsent(
            cls, r -> (BotArgumentConverter<Object, T>) ReflectionUtils.newInstance(r));
  }
}
