package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.boost.ChatBoostUpdated;
import org.telegram.telegrambots.meta.api.objects.inlinequery.ChosenInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;
import org.telegram.telegrambots.meta.api.objects.payments.PreCheckoutQuery;
import org.telegram.telegrambots.meta.api.objects.payments.ShippingQuery;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollAnswer;
import org.telegram.telegrambots.meta.api.objects.reactions.MessageReactionCountUpdated;
import org.telegram.telegrambots.meta.api.objects.reactions.MessageReactionUpdated;

/**
 * Перечень поддерживаемых типов обновлений Telegram + сведения, обязателен ли userId / chatId для
 * данного события.
 */
public enum BotRequestType {

  /* Update на вход */
  // user  chat
  ANY(Update.class, false, false),

  /* Сообщения в чатах */
  MESSAGE(Message.class, true, true),
  EDITED_MESSAGE(Message.class, true, true),

  /* Публикации в каналах */
  CHANNEL_POST(Message.class, false, true),
  EDITED_CHANNEL_POST(Message.class, false, true),

  /* Inline-клавиатура */
  CALLBACK_QUERY(CallbackQuery.class, true, false),

  /* Инлайновые запросы */
  INLINE_QUERY(InlineQuery.class, true, false),
  CHOSEN_INLINE_QUERY(ChosenInlineQuery.class, true, false),

  /* Платёжные события */
  SHIPPING_QUERY(ShippingQuery.class, true, false),
  PRE_CHECKOUT_QUERY(PreCheckoutQuery.class, true, false),

  /* Опросы */
  POLL(Poll.class, false, false),
  POLL_ANSWER(PollAnswer.class, true, false),

  /* Изменения статусов участников */
  CHAT_MEMBER(ChatMemberUpdated.class, true, true),
  MY_CHAT_MEMBER(ChatMemberUpdated.class, true, true),
  CHAT_JOIN_REQUEST(ChatJoinRequest.class, true, true),

  /* Реакции */
  MESSAGE_REACTION(MessageReactionUpdated.class, true, true),
  MESSAGE_REACTION_COUNT(MessageReactionCountUpdated.class, false, true),

  /* Boost’ы (premium) */
  CHAT_BOOST(ChatBoostUpdated.class, true, true),
  REMOVED_CHAT_BOOST(ChatBoostUpdated.class, true, true);

  /** Класс Telegram API, соответствующий типу запроса. */
  private final @NonNull Class<?> type;

  /** Может ли объект содержать User-ID? */
  private final boolean hasUserId;

  /** Может ли объект содержать Chat-ID? */
  private final boolean hasChatId;

  BotRequestType(@NonNull Class<?> type, boolean hasUserId, boolean hasChatId) {
    this.type = type;
    this.hasUserId = hasUserId;
    this.hasChatId = hasChatId;
  }

  /**
   * @return true, если userId гарантированно присутствует
   */
  public boolean requiresUserId() {
    return hasUserId;
  }

  /**
   * @return true, если chatId гарантированно присутствует
   */
  public boolean requiresChatId() {
    return hasChatId;
  }

  /**
   * Проверяет, что переданный тип совместим с данным BotRequestType.
   *
   * @param clazz класс для проверки
   * @throws BotApiException если тип не совместим
   */
  @SuppressWarnings("argument")
  public void checkType(@NonNull Class<?> clazz) {
    if (!this.type.isAssignableFrom(clazz)) {
      throw new BotApiException(
          "%s is not a %s".formatted(clazz.getCanonicalName(), this.type.getSimpleName()));
    }
  }

  public @NonNull Class<?> getType() {
    return type;
  }

  public boolean isHasUserId() {
    return hasUserId;
  }

  public boolean isHasChatId() {
    return hasChatId;
  }
}
