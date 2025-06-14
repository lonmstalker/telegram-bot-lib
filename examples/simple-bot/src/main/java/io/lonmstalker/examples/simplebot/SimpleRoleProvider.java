package io.lonmstalker.examples.simplebot;

import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

public class SimpleRoleProvider implements BotUserProvider {

    @Override
    public @NonNull BotUserInfo resolve(Update update) {
        User user = update.getMessage() != null
                ? update.getMessage().getFrom()
                : update.getCallbackQuery() != null
                ? update.getCallbackQuery().getFrom()
                : update.getInlineQuery().getFrom();
        if (user == null) {
            throw new BotApiException("User not found");
        }
        String chatId = user.getId().toString();
        return new SimpleInfo(chatId, Set.of("ADMIN"));
    }

    private record SimpleInfo(String chatId, Set<String> roles) implements BotUserInfo {
    }
}
