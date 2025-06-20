package io.lonmstalker.tgkit.testkit;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.junit.jupiter.api.extension.ExtendWith;

/** Упрощённая аннотация для подключения {@link BotTestExtension}. */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@ExtendWith(BotTestExtension.class)
public @interface TelegramBotTest {}
