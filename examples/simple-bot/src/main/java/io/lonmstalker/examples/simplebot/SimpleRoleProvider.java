package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

public class SimpleRoleProvider implements BotUserProvider {

    @Override
    public @NonNull BotUserInfo resolve(Update update) {
        User user = null;
        if (update.getMessage() != null) {
            user = update.getMessage().getFrom();
        } else if (update.getCallbackQuery() != null) {
            user = update.getCallbackQuery().getFrom();
        } else if (update.getInlineQuery() != null) {
            user = update.getInlineQuery().getFrom();
        }
        if (user == null) {
            throw new BotApiException("User not found in update");
        }
        String chatId = user.getId().toString();
        return new SimpleInfo(chatId, Set.of("ADMIN"));
    }

    private record SimpleInfo(String chatId, Set<String> roles) implements BotUserInfo {
    }
}
