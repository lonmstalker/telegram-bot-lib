package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

public class SimpleRoleProvider implements BotUserProvider {

    @Override
    public @NonNull BotUserInfo resolve(@NonNull Update update) {
        var userId = resolveUserId(update);
        var chatId = resolveChatId(update);
        return new SimpleInfo(chatId, userId, null, Set.of());
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

    private record SimpleInfo(@Nullable Long chatId,
                              @Nullable Long userId,
                              @Nullable Long internalUserId,
                              Set<String> roles) implements BotUserInfo {
    }
}
