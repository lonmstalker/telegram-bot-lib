package io.lonmstalker.examples.simplebot;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.MessageTextMatch;
import io.lonmstalker.tgkit.core.annotation.CallbackRegexMatch;
import io.lonmstalker.tgkit.core.annotation.AlwaysMatch;
import io.lonmstalker.tgkit.core.wizard.AnnotatedWizard;
import io.lonmstalker.tgkit.core.wizard.WizardEngine;
import io.lonmstalker.tgkit.core.wizard.WizardMeta;
import io.lonmstalker.tgkit.core.wizard.annotation.*;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;

/** Пример аннотированного Wizard. */
@Wizard(id = "order")
public class OrderWizard {

    private final WizardEngine engine = new WizardEngine();
    private final WizardMeta meta = AnnotatedWizard.parse(OrderWizard.class);

    @BotHandler(type = BotRequestType.MESSAGE)
    @MessageTextMatch("/order2")
    @WizardStart
    public BotResponse startMsg(BotRequest<Message> req) {
        return engine.handle(req, meta);
    }

    @BotHandler(type = BotRequestType.CALLBACK_QUERY)
    @CallbackRegexMatch("^start_order$")
    @WizardStart
    public BotResponse startCb(BotRequest<CallbackQuery> req) {
        return engine.handle(req, meta);
    }

    @BotHandler(type = BotRequestType.MESSAGE)
    @AlwaysMatch
    public BotResponse flowMsg(BotRequest<Message> req) {
        String key = req.data().getChatId() + ":" + meta.id();
        if (req.botInfo().store().get(key) != null) {
            return engine.handle(req, meta);
        }
        return null;
    }

    @BotHandler(type = BotRequestType.CALLBACK_QUERY)
    @AlwaysMatch
    public BotResponse flowCb(BotRequest<CallbackQuery> req) {
        String key = req.data().getMessage().getChatId() + ":" + meta.id();
        if (req.botInfo().store().get(key) != null) {
            return engine.handle(req, meta);
        }
        return null;
    }

    @Step(order = 0, askKey = "wizard.name", defaultAsk = "Введите товар:", saveKey = "name")
    public void name(@Answer String name) {}

    @Step(order = 1, askKey = "wizard.qty", defaultAsk = "Количество:", saveKey = "qty")
    public void qty(@Answer String qty) {}

    @Finish
    public void done() {}
}
