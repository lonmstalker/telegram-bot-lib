package io.lonmstalker.tgkit.core.user;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
import lombok.AllArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

public class SimpleUserProvider implements BotUserProvider {

    @Override
    public @NonNull BotUserInfo resolve(@NonNull Update update) {
        Long userId = UpdateUtils.resolveUserId(update);
        Long chatId = UpdateUtils.resolveChatId(update);
        return new SimpleBotUserInfo(userId, chatId);
    }

    @AllArgsConstructor
    static class SimpleBotUserInfo implements BotUserInfo {
        private @Nullable Long userId;
        private @Nullable Long chatId;

        @Override
        public @Nullable Long chatId() {
            return chatId;
        }

        @Override
        public @Nullable Long userId() {
            return userId;
        }

        @Override
        public @Nullable Long internalUserId() {
            return null;
        }

        @Override
        public @NonNull Set<String> roles() {
            return Set.of();
        }
    }
}
