package io.lonmstalker.tgkit.core.exception;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

@Slf4j
public class BotExceptionHandlerDefault implements BotExceptionHandler {
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
