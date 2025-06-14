package io.lonmstalker.examples.simplebot;

import io.lonmstalker.core.user.BotUserInfo;
import io.lonmstalker.core.user.BotUserProvider;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import java.util.Set;

public class SimpleRoleProvider implements BotUserProvider {
    @Override
    public BotUserInfo resolve(Update update) {
        User user = update.getMessage() != null ? update.getMessage().getFrom() : update.getCallbackQuery() != null ? update.getCallbackQuery().getFrom() : update.getInlineQuery().getFrom();
        String chatId = user.getId().toString();
        return new SimpleInfo(chatId, Set.of("ADMIN"));
    }

    private record SimpleInfo(String chatId, Set<String> roles) implements BotUserInfo {}
}
