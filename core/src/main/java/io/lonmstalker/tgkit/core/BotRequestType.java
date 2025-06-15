package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.ChatJoinRequest;
import org.telegram.telegrambots.meta.api.objects.ChatMemberUpdated;
import org.telegram.telegrambots.meta.api.objects.Message;
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
 * Перечень поддерживаемых типов обновлений Telegram.
 */
@Getter
public enum BotRequestType {
    /** Обычные сообщения и их правки **/
    MESSAGE(Message.class),
    EDITED_MESSAGE(Message.class),
    /** Публикации в каналах и их правки **/
    CHANNEL_POST(Message.class),
    EDITED_CHANNEL_POST(Message.class),
    /** Пользователь вводит адрес доставки при платежах через бот **/
    SHIPPING_QUERY(ShippingQuery.class),
    /** Перед финальной оплатой, когда пользователь подтверждает сумму **/
    PRE_CHECKOUT_QUERY(PreCheckoutQuery.class),
    /** Опросы и ответы на опросы **/
    POLL(Poll.class),
    POLL_ANSWER(PollAnswer.class),
    /** Изменения статуса участников чата **/
    CHAT_MEMBER(ChatMemberUpdated.class),
    MY_CHAT_MEMBER(ChatMemberUpdated.class),
    CHAT_JOIN_REQUEST(ChatJoinRequest.class),
    /** Возникает при нажатии кнопок InlineKeyboard с параметром callback_data **/
    CALLBACK_QUERY(CallbackQuery.class),
    /** Пользователь набирает @yourbot в любом чате и запрашивает данные инлайн-поиска **/
    INLINE_QUERY(InlineQuery.class),
    /** Выбор результата инлайн-поиска **/
    CHOSEN_INLINE_QUERY(ChosenInlineQuery.class),
    /** Обновление при добавлении, изменении или удалении неанонимной реакции на сообщение **/
    MESSAGE_REACTION(MessageReactionUpdated.class),
    /** Обновление при изменении общего количества (анонимных) реакций на сообщение **/
    MESSAGE_REACTION_COUNT(MessageReactionCountUpdated.class),
    /** Уведомление о том, что пользователь “прокачал” (boosted) чат, например, оформив премиум-подписку. **/
    CHAT_BOOST(ChatBoostUpdated.class),
    /** Уведомление о снятии ранее оформленного “буста” чата **/
    REMOVED_CHAT_BOOST(ChatBoostUpdated.class);

    /** Класс Telegram API, соответствующий типу запроса. */
    private final @NonNull Class<?> type;

    BotRequestType(@NonNull Class<?> type) {
        this.type = type;
    }

    /**
     * Проверяет, что переданный тип соответствует ожидаемому.
     *
     * @param type класс для проверки
     * @throws BotApiException если тип не совместим
     */
    public void checkType(Class<?> type) {
        if (!this.type.isAssignableFrom(type)) {
            throw new BotApiException(String.format("%s is not a %s", this.type.getSimpleName(), type.getCanonicalName()));
        }
    }
}
