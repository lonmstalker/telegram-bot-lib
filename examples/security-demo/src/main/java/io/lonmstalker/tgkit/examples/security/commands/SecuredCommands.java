package io.lonmstalker.tgkit.examples.security.commands;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.AlwaysMatch;
import io.lonmstalker.tgkit.security.RateLimit;
import io.lonmstalker.tgkit.security.LimiterKey;
import io.lonmstalker.tgkit.security.Roles;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;

public class SecuredCommands {

    @BotHandler(type = BotRequestType.MESSAGE)
    @AlwaysMatch
    @Roles("ADMIN")
    @RateLimit(key = LimiterKey.USER, permits = 3, seconds = 60)
    public BotResponse secret(BotRequest<Message> req) {
        SendMessage send = new SendMessage(req.data().getChatId().toString(), "secret");
        return BotResponse.builder().method(send).build();
    }
}
