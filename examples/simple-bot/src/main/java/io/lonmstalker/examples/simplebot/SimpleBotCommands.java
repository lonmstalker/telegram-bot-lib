package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.annotation.AlwaysMatch;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.MessageContainsMatch;
import io.lonmstalker.tgkit.core.annotation.MessageRegexMatch;
import io.lonmstalker.tgkit.core.annotation.MessageTextMatch;
import io.lonmstalker.tgkit.core.annotation.UserRoleMatch;
import io.lonmstalker.tgkit.core.wizard.WizardBuilder;
import io.lonmstalker.tgkit.core.wizard.WizardEngine;
import io.lonmstalker.tgkit.core.wizard.WizardMeta;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

import java.util.Collections;

public class SimpleBotCommands {

    private final WizardEngine wizard = new WizardEngine();
    private final WizardMeta orderWizard = new WizardBuilder("order")
            .step("wizard.name", "Введите товар:", "name", v -> !v.isBlank())
            .step("wizard.qty", "Количество:", "qty", v -> v.matches("\d+"))
            .build();

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
    @MessageTextMatch("/order")
    public BotResponse orderStart(BotRequest<Message> request) {
        return wizard.handle(request, orderWizard);
    }

    @BotHandler(type = BotRequestType.MESSAGE)
    @AlwaysMatch
    public BotResponse orderFlow(BotRequest<Message> request) {
        String key = request.data().getChatId() + ":" + orderWizard.id();
        if (request.botInfo().store().get(key) != null) {
            return wizard.handle(request, orderWizard);
        }
        return null;
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
