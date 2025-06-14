package io.lonmstalker.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of {@link BotUserRepository}.
 */
public class InMemoryBotUserRepository implements BotUserRepository {
    private final Map<Long, BotUserInfo> users = new ConcurrentHashMap<>();

    @Override
    public @NonNull BotUserInfo getOrCreate(@NonNull User telegramUser) {
        return users.computeIfAbsent(telegramUser.getId(), id ->
                new DefaultUserInfo(String.valueOf(id), Set.of()));
    }

    private record DefaultUserInfo(@NonNull String chatId,
                                   @NonNull Set<String> roles) implements BotUserInfo {
    }
}
