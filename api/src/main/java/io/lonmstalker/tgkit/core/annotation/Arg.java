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
package io.lonmstalker.tgkit.core.annotation;

import io.lonmstalker.tgkit.core.args.BotArgumentConverter;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.nullness.qual.NonNull;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
public @interface Arg {

  /** Имя именованной regex-группы из ближайшего матчера. */
  @NonNull String value() default "";

  /** Обязательность; для примитивов по умолчанию false запрещён. */
  boolean required() default true;

  /** Строковый дефолт; применяется до конвертации. */
  String defaultValue() default "";

  /** Пользовательский конвертер; Identity = авто-выбор по типу. */
  Class<? extends BotArgumentConverter<?, ?>> converter() default
      BotArgumentConverter.UpdateConverter.class;
}
