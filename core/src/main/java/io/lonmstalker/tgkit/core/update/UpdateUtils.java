package io.lonmstalker.tgkit.core.update;

import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

@UtilityClass
public class UpdateUtils {

    /**
     * Таблица, сопоставляющая условие из Update типу запроса.
     */
    @SuppressWarnings("method.invocation")
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

    /**
     * Функции, извлекающие пользователя из update.
     */
    @SuppressWarnings("argument")
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

    public static @Nullable Long resolveUserId(@NonNull Update update) {
        User user = null;

        // 1. Обычное сообщение / контакт
        if (update.getMessage() != null) {
            user = update.getMessage().getFrom();
        } else if (update.getEditedMessage() != null) {
            user = update.getEditedMessage().getFrom();
        }
        // 2. Callback-клавиатуры
        else if (update.getCallbackQuery() != null) {
            user = update.getCallbackQuery().getFrom();
        }
        // 3. Инлайн-запросы
        else if (update.getInlineQuery() != null) {
            user = update.getInlineQuery().getFrom();
        } else if (update.getChosenInlineQuery() != null) {
            user = update.getChosenInlineQuery().getFrom();
        }
        // 4. Платёжные события
        else if (update.getPreCheckoutQuery() != null) {
            user = update.getPreCheckoutQuery().getFrom();
        } else if (update.getShippingQuery() != null) {
            user = update.getShippingQuery().getFrom();
        }
        // 5. События членства
        else if (update.getMyChatMember() != null) {
            user = update.getMyChatMember().getFrom();
        } else if (update.getChatMember() != null) {
            user = update.getChatMember().getFrom();
        } else if (update.getChatJoinRequest() != null) {
            user = update.getChatJoinRequest().getUser();
        }

        return user == null ? null : user.getId();
    }

    /**
     * Извлекает chat_id из {@link Update}.
     */
    public static @Nullable Long resolveChatId(@NonNull Update update) {
        if (update.getMessage() != null) {
            return update.getMessage().getChatId();
        } else if (update.getEditedMessage() != null) {
            return update.getEditedMessage().getChatId();
        } else if (update.getCallbackQuery() != null) {
            return update.getCallbackQuery().getMessage().getChatId();
        } else if (update.getChannelPost() != null) {
            return update.getChannelPost().getChatId();
        } else if (update.getEditedChannelPost() != null) {
            return update.getEditedChannelPost().getChatId();
        } else if (update.getChatMember() != null) {
            return update.getChatMember().getChat().getId();
        } else if (update.getMyChatMember() != null) {
            return update.getMyChatMember().getChat().getId();
        } else if (update.getChatJoinRequest() != null) {
            return update.getChatJoinRequest().getChat().getId();
        }
        // InlineQuery, PreCheckoutQuery, ShippingQuery и другие события не содержат chat_id.
        return null;
    }


    public static @Nullable Integer resolveMessageId(@NonNull Update u) {

        /* обычные сообщения */
        if (u.getMessage() != null) return u.getMessage().getMessageId();
        if (u.getEditedMessage() != null) return u.getEditedMessage().getMessageId();

        /* посты в каналах */
        if (u.getChannelPost() != null) return u.getChannelPost().getMessageId();
        if (u.getEditedChannelPost() != null) return u.getEditedChannelPost().getMessageId();

        /* callback-кнопки */
        if (u.getCallbackQuery() != null) {
            MaybeInaccessibleMessage m = u.getCallbackQuery().getMessage();
            return m != null ? m.getMessageId() : null;
        }

        /* реакции (анонимные и публичные) */
        if (u.getMessageReaction() != null)
            return u.getMessageReaction().getMessageId();
        if (u.getMessageReactionCount() != null)
            return u.getMessageReactionCount().getMessageId();

        /* в остальных апдейтах (inline-query, poll, shipping…) messageId отсутствует */
        return null;
    }
}
