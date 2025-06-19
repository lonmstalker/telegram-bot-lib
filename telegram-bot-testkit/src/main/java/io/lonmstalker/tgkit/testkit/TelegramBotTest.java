package io.lonmstalker.tgkit.testkit;

import org.junit.jupiter.api.extension.ExtendWith;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Упрощённая аннотация для подключения {@link BotTestExtension}.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(BotTestExtension.class)
public @interface TelegramBotTest {}
