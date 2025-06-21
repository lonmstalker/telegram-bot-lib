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
package io.github.tgkit.internal.loader;

import io.github.tgkit.internal.BotCommand;
import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Фабрика, позволяющая расширять {@link BotCommand}.
 *
 * @param <A> тип аннотации, по которой срабатывает фабрика
 */
public interface BotCommandFactory<A extends Annotation> {

  /**
   * @return класс аннотации, по которой нужно применить этот фабричный алгоритм(null == любая)
   */
  @SuppressWarnings("unchecked")
  default @NonNull Class<A> annotationType() {
    return (Class<A>) None.class;
  }

  /**
   * Вызывается при обнаружении аннотации {@linkplain #annotationType()} на методе-хендлере.
   * Доступна команда и сам метод.
   *
   * @param command команда {@link BotCommand}
   * @param method метод-хендлер
   * @param ann экземпляр аннотации
   */
  void apply(@NonNull BotCommand<?> command, @NonNull Method method, @Nullable A ann);

  /** BotCommandFactory применяется на все команды */
  @Target(ElementType.TYPE)
  @Retention(RetentionPolicy.RUNTIME)
  @interface None {}
}
