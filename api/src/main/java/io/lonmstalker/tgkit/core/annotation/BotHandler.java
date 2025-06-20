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
