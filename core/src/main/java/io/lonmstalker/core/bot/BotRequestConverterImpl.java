package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotRequestConverter;
import io.lonmstalker.core.BotRequestType;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

public class BotRequestConverterImpl implements BotRequestConverter<BotApiObject> {

    @Override
    public @NonNull BotApiObject convert(@NonNull Update update, @NonNull BotRequestType type) {
        return switch (type) {
            case MESSAGE -> update.getMessage();
            case EDITED_MESSAGE -> update.getEditedMessage();
            case CHANNEL_POST -> update.getChannelPost();
            case EDITED_CHANNEL_POST -> update.getEditedChannelPost();
            case SHIPPING_QUERY -> update.getShippingQuery();
            case PRE_CHECKOUT_QUERY -> update.getPreCheckoutQuery();
            case POLL -> update.getPoll();
            case POLL_ANSWER -> update.getPollAnswer();
            case CHAT_MEMBER -> update.getChatMember();
            case MY_CHAT_MEMBER -> update.getMyChatMember();
            case CHAT_JOIN_REQUEST -> update.getChatJoinRequest();
            case CALLBACK_QUERY -> update.getCallbackQuery();
            case INLINE_QUERY -> update.getInlineQuery();
            case CHOSEN_INLINE_QUERY -> update.getChosenInlineQuery();
            case MESSAGE_REACTION -> update.getMessageReaction();
            case MESSAGE_REACTION_COUNT -> update.getMessageReactionCount();
            case CHAT_BOOST -> update.getChatBoost();
            case REMOVED_CHAT_BOOST -> update.getRemovedChatBoost();
        };
    }
}
