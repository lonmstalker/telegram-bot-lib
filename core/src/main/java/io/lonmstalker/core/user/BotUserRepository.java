package io.lonmstalker.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Interface for loading or creating user information in database.
 */
public interface BotUserRepository {
    /**
     * Returns existing user info or creates a new one based on telegram user.
     */
    @NonNull BotUserInfo getOrCreate(@NonNull User telegramUser);
}
