/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.reflection;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class ReflectionUtils {
  private ReflectionUtils() {}

  /** Создаёт экземпляр указанного класса, учитывая возможный метод getInstance(). */
  @SuppressWarnings({"return", "unchecked", "argument"})
  public static <T> @NonNull T newInstance(@NonNull Class<T> clazz) {
    try {
      Method getInstance = clazz.getDeclaredMethod("getInstance");
      if (Modifier.isStatic(getInstance.getModifiers())) {
        getInstance.setAccessible(true);
        return (T) getInstance.invoke(null);
      }
    } catch (NoSuchMethodException ignored) {
      // no getInstance method
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new BotApiException("Cannot invoke getInstance for " + clazz.getName(), e);
    }

    try {
      var ctor =
          MethodHandles.lookup()
              .findConstructor(clazz, MethodType.methodType(void.class))
              .asType(MethodType.methodType(Object.class));
      return (T) ctor.invokeExact();
    } catch (Throwable e) {
      throw new BotApiException("Cannot instantiate " + clazz.getName(), e);
    }
  }
}
