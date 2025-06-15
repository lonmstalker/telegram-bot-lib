package io.lonmstalker.tgkit.render.telegram;

import io.craftbot.render.spi.ResponseRenderer;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;

public class PhotoRenderer implements ResponseRenderer<PhotoMsg> {
    @Override
    public boolean supports(Object o) {
        return o instanceof PhotoMsg;
    }

    @Override
    public BotResponse render(PhotoMsg msg, Context ctx) {
        SendPhoto p = new SendPhoto(ctx.request().botInfo().internalId()+"", msg.file());
        p.setCaption(msg.caption());
        return BotResponse.builder().method(p).build();
    }
}
