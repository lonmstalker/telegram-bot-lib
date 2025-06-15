package io.lonmstalker.tgkit.render.telegram;

import io.craftbot.render.spi.ResponseRenderer;
import io.craftbot.security.spi.CaptchaChallenge;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class CaptchaRenderer implements ResponseRenderer<CaptchaChallenge> {
    @Override
    public boolean supports(Object o) {
        return o instanceof CaptchaChallenge;
    }

    @Override
    public BotResponse render(CaptchaChallenge c, Context ctx) {
        if (c.photo() != null) {
            SendPhoto p = new SendPhoto(ctx.request().botInfo().internalId()+"", c.photo());
            p.setCaption(c.message());
            p.setReplyMarkup(c.keyboard());
            return BotResponse.builder().method(p).build();
        }
        SendMessage m = new SendMessage(ctx.request().botInfo().internalId()+"", c.message());
        m.setReplyMarkup(c.keyboard());
        return BotResponse.builder().method(m).build();
    }

    @Override
    public int order() {
        return 10;
    }
}
