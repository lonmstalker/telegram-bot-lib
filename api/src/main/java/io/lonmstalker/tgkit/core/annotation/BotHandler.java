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

import io.lonmstalker.tgkit.core.BotCommandOrder;
import io.lonmstalker.tgkit.core.BotHandlerConverter;
import io.lonmstalker.tgkit.core.BotRequestType;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.checkerframework.checker.nullness.qual.NonNull;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface BotHandler {

  /** Идентификатор команды Дефолтное значение: генерируется автоматически */
  String id() default "";

  /** Тип сообщения на вход Дефолтное значение: MESSAGE */
  @NonNull BotRequestType type() default BotRequestType.MESSAGE;

  /** Группировка команд для использования ботом Дефолтное значение: доступно всем */
  String botGroup() default "";

  /** Конвертер входящего BotRequest Дефолтное значение: возвращает BotRequest */
  Class<? extends BotHandlerConverter<?>> converter() default BotHandlerConverter.Identity.class;

  /** Порядок проверки команд Дефолтное значение: LAST */
  int order() default BotCommandOrder.LAST;
}
