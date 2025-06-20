package io.lonmstalker.tgkit.core.bot;

import static io.lonmstalker.tgkit.core.bot.BotConstants.BOT_TOKEN_SECRET;

import io.lonmstalker.tgkit.core.BotAdapter;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandler;
import io.lonmstalker.tgkit.core.exception.BotExceptionHandlerDefault;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.bots.TelegramWebhookBot;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

class WebHookReceiver extends TelegramWebhookBot implements AutoCloseable {
  private static final Logger log = LoggerFactory.getLogger(WebHookReceiver.class);
  private final @NonNull String token;
  private final @NonNull BotAdapter adapter;
  private final @NonNull TelegramSender sender;
  private final @NonNull BotExceptionHandler globalExceptionHandler;

  private @Nullable String username;

  void setUsername(@Nullable String username) {
    this.username = username;
  }

  public WebHookReceiver(
      @NonNull BotConfig options,
      @NonNull BotAdapter adapter,
      @NonNull SecretStore store,
      @NonNull TelegramSender sender,
      @Nullable BotExceptionHandler globalExceptionHandler) {
    this(
        options,
        adapter,
        store
            .get(BOT_TOKEN_SECRET)
            .orElseThrow(() -> new IllegalArgumentException("secret 'bot_token' not found")),
        sender,
        globalExceptionHandler);
  }

  public WebHookReceiver(
      @NonNull BotConfig options,
      @NonNull BotAdapter adapter,
      @NonNull String token,
      @NonNull TelegramSender sender,
      @Nullable BotExceptionHandler globalExceptionHandler) {
    super(options, token);
    this.token = token;
    this.sender = sender;
    this.adapter = adapter;
    this.globalExceptionHandler =
        globalExceptionHandler != null
            ? globalExceptionHandler
            : BotExceptionHandlerDefault.INSTANCE;
    if (adapter instanceof BotAdapterImpl b) {
      b.setSender(sender);
    }
  }

  @Override
  public @NonNull String getBotUsername() {
    return username != null ? username : StringUtils.EMPTY;
  }

  @Override
  @SuppressWarnings("override.return")
  public @Nullable BotApiMethod<?> onWebhookUpdateReceived(Update update) {
    try {
      return adapter.handle(update);
    } catch (Exception e) {
      return globalExceptionHandler.handle(update, e);
    }
  }

  @Override
  public String getBotPath() {
    return token;
  }

  @Override
  public void close() {
    if (adapter instanceof AutoCloseable) {
      try {
        ((AutoCloseable) adapter).close();
      } catch (Exception e) {
        // ignored
      }
    }
  }
}
