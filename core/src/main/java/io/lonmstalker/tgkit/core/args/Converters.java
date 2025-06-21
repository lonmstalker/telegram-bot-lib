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

package io.github.tgkit.core.args;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.reflection.ReflectionUtils;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Утилитарный реестр, где хранятся все кастомные BotArgumentConverter.
 */
public final class Converters {
  private static final Map<Class<?>, BotArgumentConverter<?, ?>> BY_TYPE =
      new ConcurrentHashMap<>();
  private static final Map<Class<?>, BotArgumentConverter<?, ?>> BY_CLASS =
      new ConcurrentHashMap<>();

  static {
    register(Update.class, new BotArgumentConverter.UpdateConverter());
    register(BotRequest.class, new BotArgumentConverter.RequestConverter());
    // ... добавляем Number/Boolean/Enum и т.п.
  }

  private Converters() {
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
