package io.craftbot.render.spi;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;

public interface ResponseRenderer<T> {
    boolean supports(Object obj);
    BotResponse render(T obj, Context ctx) throws Exception;
    default int order() { return 50; }
}
