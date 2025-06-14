package io.lonmstalker.core.utils;

import io.lonmstalker.core.BotRequestType;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

@UtilityClass
public class UpdateUtils {

    public static @NonNull BotRequestType getType(@NonNull Update update) {
        if (update.getMessage() != null) {
            return BotRequestType.MESSAGE;
        }
        if (update.getEditedMessage() != null) {
            return BotRequestType.EDITED_MESSAGE;
        }
        if (update.getChannelPost() != null) {
            return BotRequestType.CHANNEL_POST;
        }
        if (update.getEditedChannelPost() != null) {
            return BotRequestType.EDITED_CHANNEL_POST;
        }
        if (update.getShippingQuery() != null) {
            return BotRequestType.SHIPPING_QUERY;
        }
        if (update.getPreCheckoutQuery() != null) {
            return BotRequestType.PRE_CHECKOUT_QUERY;
        }
        if (update.getPoll() != null) {
            return BotRequestType.POLL;
        }
        if (update.getPollAnswer() != null) {
            return BotRequestType.POLL_ANSWER;
        }
        if (update.getChatMember() != null) {
            return BotRequestType.CHAT_MEMBER;
        }
        if (update.getMyChatMember() != null) {
            return BotRequestType.MY_CHAT_MEMBER;
        }
        if (update.getChatJoinRequest() != null) {
            return BotRequestType.CHAT_JOIN_REQUEST;
        }
        if (update.getCallbackQuery() != null) {
            return BotRequestType.CALLBACK_QUERY;
        }
        if (update.getInlineQuery() != null) {
            return BotRequestType.INLINE_QUERY;
        }
        if (update.getChosenInlineQuery() != null) {
            return BotRequestType.CHOSEN_INLINE_QUERY;
        }
        if (update.getMessageReaction() != null) {
            return BotRequestType.MESSAGE_REACTION;
        }
        if (update.getMessageReactionCount() != null) {
            return BotRequestType.MESSAGE_REACTION_COUNT;
        }
        if (update.getChatBoost() != null) {
            return BotRequestType.CHAT_BOOST;
        }
        if (update.getRemovedChatBoost() != null) {
            return BotRequestType.REMOVED_CHAT_BOOST;
        }

        throw new IllegalArgumentException("Unknown update type");
    }

    /**
     * Attempts to extract {@link org.telegram.telegrambots.meta.api.objects.User} from update.
     *
     * @throws IllegalArgumentException when no user information can be resolved
     */
    public static @NonNull User getUser(@NonNull Update update) {
        if (update.getMessage() != null && update.getMessage().getFrom() != null) {
            return update.getMessage().getFrom();
        }
        if (update.getEditedMessage() != null && update.getEditedMessage().getFrom() != null) {
            return update.getEditedMessage().getFrom();
        }
        if (update.getChannelPost() != null && update.getChannelPost().getFrom() != null) {
            return update.getChannelPost().getFrom();
        }
        if (update.getEditedChannelPost() != null && update.getEditedChannelPost().getFrom() != null) {
            return update.getEditedChannelPost().getFrom();
        }
        if (update.getCallbackQuery() != null && update.getCallbackQuery().getFrom() != null) {
            return update.getCallbackQuery().getFrom();
        }
        if (update.getInlineQuery() != null) {
            return update.getInlineQuery().getFrom();
        }
        if (update.getChosenInlineQuery() != null) {
            return update.getChosenInlineQuery().getFrom();
        }
        throw new IllegalArgumentException("User not found in update");
    }
}
