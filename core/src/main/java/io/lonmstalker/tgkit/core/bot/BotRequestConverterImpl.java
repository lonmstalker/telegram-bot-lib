package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotRequestConverter;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import lombok.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Реестр конвертеров для каждого BotRequestType вместо большого switch.
 */
class BotRequestConverterImpl implements BotRequestConverter<BotApiObject> {

    private final Map<BotRequestType, Function<Update, BotApiObject>> registry = new EnumMap<>(BotRequestType.class);

    BotRequestConverterImpl() {
        registry.put(BotRequestType.MESSAGE, Update::getMessage);
        registry.put(BotRequestType.EDITED_MESSAGE, Update::getEditedMessage);
        registry.put(BotRequestType.CHANNEL_POST, Update::getChannelPost);
        registry.put(BotRequestType.EDITED_CHANNEL_POST, Update::getEditedChannelPost);
        registry.put(BotRequestType.CALLBACK_QUERY, Update::getCallbackQuery);
        registry.put(BotRequestType.INLINE_QUERY, Update::getInlineQuery);
        registry.put(BotRequestType.CHOSEN_INLINE_QUERY, Update::getChosenInlineQuery);
        registry.put(BotRequestType.SHIPPING_QUERY, Update::getShippingQuery);
        registry.put(BotRequestType.PRE_CHECKOUT_QUERY, Update::getPreCheckoutQuery);
        registry.put(BotRequestType.POLL, Update::getPoll);
        registry.put(BotRequestType.POLL_ANSWER, Update::getPollAnswer);
        registry.put(BotRequestType.CHAT_MEMBER, Update::getChatMember);
        registry.put(BotRequestType.MY_CHAT_MEMBER, Update::getMyChatMember);
        registry.put(BotRequestType.CHAT_JOIN_REQUEST, Update::getChatJoinRequest);
        registry.put(BotRequestType.MESSAGE_REACTION, Update::getMessageReaction);
        registry.put(BotRequestType.MESSAGE_REACTION_COUNT, Update::getMessageReactionCount);
        registry.put(BotRequestType.CHAT_BOOST, Update::getChatBoost);
        registry.put(BotRequestType.REMOVED_CHAT_BOOST, Update::getRemovedChatBoost);
    }

    @Override
    public @NonNull BotApiObject convert(@NonNull Update update,
                                         @NonNull BotRequestType type) {
        Function<Update, BotApiObject> fn = registry.get(type);
        if (fn == null) {
            throw new BotApiException("Unsupported request type: " + type);
        }
        BotApiObject obj = fn.apply(update);
        type.checkType(obj.getClass());
        return obj;
    }
}
