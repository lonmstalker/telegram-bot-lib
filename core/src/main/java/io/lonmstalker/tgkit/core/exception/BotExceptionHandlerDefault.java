package io.lonmstalker.tgkit.core.exception;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotExceptionHandlerDefault implements BotExceptionHandler {
  private static final Logger log = LoggerFactory.getLogger(BotExceptionHandlerDefault.class);
  public static final BotExceptionHandler INSTANCE = new BotExceptionHandlerDefault();

  @Override
  public @Nullable BotApiMethod<?> handle(@NonNull Update update, @NonNull Exception ex) {
    log.error("onUpdate with error: ", ex);

    Long chatId = UpdateUtils.resolveChatId(update);
    if (chatId == null) {
      return null;
    }

    return SendMessage.builder()
        .chatId(chatId)
        .text("error.internal")
        .disableNotification(true)
        .build();
  }
}
