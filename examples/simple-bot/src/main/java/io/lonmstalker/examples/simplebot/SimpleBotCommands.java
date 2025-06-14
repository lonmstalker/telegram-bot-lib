package io.lonmstalker.examples.simplebot;

import io.lonmstalker.core.BotRequest;
import io.lonmstalker.core.BotResponse;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.annotation.AlwaysMatch;
import io.lonmstalker.core.annotation.BotHandler;
import io.lonmstalker.core.annotation.MessageContainsMatch;
import io.lonmstalker.core.annotation.MessageRegexMatch;
import io.lonmstalker.core.annotation.MessageTextMatch;
import io.lonmstalker.core.annotation.UserRoleMatch;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.Collections;

public class SimpleBotCommands {

    @BotHandler(type = BotRequestType.MESSAGE)
    @MessageTextMatch("ping")
    public BotResponse ping(BotRequest<Message> request) {
        SendMessage send = new SendMessage(request.data().getChatId().toString(), "pong");
        return BotResponse.builder().method(send).build();
    }

    @BotHandler(type = BotRequestType.MESSAGE)
    @MessageContainsMatch("hello")
    public BotResponse hello(BotRequest<Message> request) {
        SendMessage send = new SendMessage(request.data().getChatId().toString(), "Hello!");
        return BotResponse.builder().method(send).build();
    }

    @BotHandler(type = BotRequestType.MESSAGE)
    @MessageRegexMatch(".*\\d+.*")
    public BotResponse numbers(BotRequest<Message> request) {
        SendMessage send = new SendMessage(request.data().getChatId().toString(), "numbers");
        return BotResponse.builder().method(send).build();
    }

    @BotHandler(type = BotRequestType.MESSAGE)
    @UserRoleMatch(provider = SimpleRoleProvider.class, roles = {"ADMIN"})
    public BotResponse admin(BotRequest<Message> request) {
        SendMessage send = new SendMessage(request.data().getChatId().toString(), "admin");
        return BotResponse.builder().method(send).build();
    }

    @BotHandler(type = BotRequestType.CALLBACK_QUERY)
    @AlwaysMatch
    public BotResponse callback(BotRequest<CallbackQuery> request) {
        AnswerCallbackQuery ans = new AnswerCallbackQuery();
        ans.setCallbackQueryId(request.data().getId());
        ans.setText("callback");
        return BotResponse.builder().method(ans).build();
    }

    @BotHandler(type = BotRequestType.INLINE_QUERY)
    @AlwaysMatch
    public BotResponse inline(BotRequest<InlineQuery> request) {
        AnswerInlineQuery ans = new AnswerInlineQuery();
        ans.setInlineQueryId(request.data().getId());
        ans.setResults(Collections.emptyList());
        return BotResponse.builder().method(ans).build();
    }
}
