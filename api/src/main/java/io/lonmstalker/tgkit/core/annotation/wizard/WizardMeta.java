package io.lonmstalker.tgkit.core.annotation.wizard;

import java.lang.annotation.*;

/**
 * Маркирует класс как wizard-сценарий.
 *
 * <p>Для регистрации движок читает эту аннотацию + все {@code @BotHandler} и matcher-аннотации на
 * классе и сам регистрирует соответствующий handler в BotDispatcher.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface WizardMeta {

  /** Уникальный идентификатор сценария. */
  String id();
}
