package io.lonmstalker.core.utils;

import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.exception.BotApiException;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class UpdateUtils {

    /** Таблица, сопоставляющая условие из Update типу запроса. */
    private static final Map<Predicate<Update>, BotRequestType> TYPE_MAP = new LinkedHashMap<>() {{
        put(u -> u.getMessage() != null, BotRequestType.MESSAGE);
        put(u -> u.getEditedMessage() != null, BotRequestType.EDITED_MESSAGE);
        put(u -> u.getChannelPost() != null, BotRequestType.CHANNEL_POST);
        put(u -> u.getEditedChannelPost() != null, BotRequestType.EDITED_CHANNEL_POST);
        put(u -> u.getShippingQuery() != null, BotRequestType.SHIPPING_QUERY);
        put(u -> u.getPreCheckoutQuery() != null, BotRequestType.PRE_CHECKOUT_QUERY);
        put(u -> u.getPoll() != null, BotRequestType.POLL);
        put(u -> u.getPollAnswer() != null, BotRequestType.POLL_ANSWER);
        put(u -> u.getChatMember() != null, BotRequestType.CHAT_MEMBER);
        put(u -> u.getMyChatMember() != null, BotRequestType.MY_CHAT_MEMBER);
        put(u -> u.getChatJoinRequest() != null, BotRequestType.CHAT_JOIN_REQUEST);
        put(u -> u.getCallbackQuery() != null, BotRequestType.CALLBACK_QUERY);
        put(u -> u.getInlineQuery() != null, BotRequestType.INLINE_QUERY);
        put(u -> u.getChosenInlineQuery() != null, BotRequestType.CHOSEN_INLINE_QUERY);
        put(u -> u.getMessageReaction() != null, BotRequestType.MESSAGE_REACTION);
        put(u -> u.getMessageReactionCount() != null, BotRequestType.MESSAGE_REACTION_COUNT);
        put(u -> u.getChatBoost() != null, BotRequestType.CHAT_BOOST);
        put(u -> u.getRemovedChatBoost() != null, BotRequestType.REMOVED_CHAT_BOOST);
    }};

    /** Функции, извлекающие пользователя из update. */
    private static final List<Function<Update, User>> USER_EXTRACTORS = List.of(
            u -> u.getMessage() != null ? u.getMessage().getFrom() : null,
            u -> u.getEditedMessage() != null ? u.getEditedMessage().getFrom() : null,
            u -> u.getChannelPost() != null ? u.getChannelPost().getFrom() : null,
            u -> u.getEditedChannelPost() != null ? u.getEditedChannelPost().getFrom() : null,
            u -> u.getCallbackQuery() != null ? u.getCallbackQuery().getFrom() : null,
            u -> u.getInlineQuery() != null ? u.getInlineQuery().getFrom() : null,
            u -> u.getChosenInlineQuery() != null ? u.getChosenInlineQuery().getFrom() : null
    );

    /**
     * Определяет тип входящего update, используя таблицу соответствия
     * предикатов и типов запроса.
     */
    public static @NonNull BotRequestType getType(@NonNull Update update) {
        return TYPE_MAP.entrySet().stream()
                .filter(e -> e.getKey().test(update))
                .findFirst()
                .map(Map.Entry::getValue)
                .orElseThrow(() -> new BotApiException("Unknown update type"));
    }

    /**
     * Attempts to extract {@link org.telegram.telegrambots.meta.api.objects.User} from update.
     *
     * @throws BotApiException when no user information can be resolved
     */
    public static @NonNull User getUser(@NonNull Update update) {
        for (var extractor : USER_EXTRACTORS) {
            User user = extractor.apply(update);
            if (user != null) {
                return user;
            }
        }
        throw new BotApiException("User not found in update");
    }
}
