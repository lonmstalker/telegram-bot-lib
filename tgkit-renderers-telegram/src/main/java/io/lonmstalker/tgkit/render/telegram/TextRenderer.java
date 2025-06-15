package io.lonmstalker.tgkit.render.telegram;

import io.craftbot.render.spi.ResponseRenderer;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public class TextRenderer implements ResponseRenderer<MarkdownMsg> {
    @Override
    public boolean supports(Object o) {
        return o instanceof MarkdownMsg;
    }

    @Override
    public BotResponse render(MarkdownMsg msg, Context ctx) {
        SendMessage m = new SendMessage(ctx.request().botInfo().internalId()+"", msg.text());
        m.enableMarkdownV2(true);
        return BotResponse.builder().method(m).build();
    }
}
