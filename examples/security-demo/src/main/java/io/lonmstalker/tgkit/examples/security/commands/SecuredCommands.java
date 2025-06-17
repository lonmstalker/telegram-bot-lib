package io.lonmstalker.tgkit.examples.security.commands;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.BotHandler;
import io.lonmstalker.tgkit.core.annotation.MessageTextMatch;
import io.lonmstalker.tgkit.security.rbac.RequiresRole;

public class SecuredCommands {

    @BotHandler
    @MessageTextMatch("hello")
    public void hello(BotRequest<?> req) { /* … */ }

    @BotHandler
    @RequiresRole("ADMIN")
    @MessageTextMatch("/ban")
    public void banCmd(BotRequest<?> req) { /* … */ }
}
