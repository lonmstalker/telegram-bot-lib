package io.craftbot.render.spi;

import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;

import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

public class ResponseDispatcher {
    private final List<ResponseRenderer<?>> renderers;

    public ResponseDispatcher(ClassLoader cl) {
        this.renderers = ServiceLoader.load(ResponseRenderer.class, cl)
                .stream()
                .map(ServiceLoader.Provider::get)
                .sorted(Comparator.comparingInt(ResponseRenderer::order))
                .toList();
    }

    public BotResponse toResponse(Object obj, Context ctx) throws Exception {
        if (obj instanceof BotResponse br) {
            return br;
        }
        for (ResponseRenderer<?> r : renderers) {
            if (r.supports(obj)) {
                return renderUnchecked(r, obj, ctx);
            }
        }
        throw new IllegalArgumentException("No renderer for " + obj.getClass());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private static BotResponse renderUnchecked(ResponseRenderer renderer, Object obj, Context ctx) throws Exception {
        return renderer.render(obj, ctx);
    }
}
