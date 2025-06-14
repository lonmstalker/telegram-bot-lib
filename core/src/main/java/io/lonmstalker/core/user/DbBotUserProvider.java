package io.lonmstalker.core.user;

import io.lonmstalker.core.utils.UpdateUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * {@link BotUserProvider} implementation that loads user info from database.
 */
public class DbBotUserProvider implements BotUserProvider {
    private final BotUserRepository repository;

    public DbBotUserProvider(@NonNull BotUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public @NonNull BotUserInfo resolve(@NonNull Update update) {
        var user = UpdateUtils.getUser(update);
        return repository.getOrCreate(user);
    }
}
