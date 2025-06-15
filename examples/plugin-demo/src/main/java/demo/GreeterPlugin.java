package demo;

import io.lonmstalker.tgkit.plugin.spi.Plugin;
import io.lonmstalker.tgkit.plugin.spi.PluginContext;

public class GreeterPlugin implements Plugin {

    private PluginContext context;

    @Override
    public int abiVersion() {
        return 1;
    }

    @Override
    public void init(PluginContext context) {
        this.context = context;

        context.bus().subscribe(msg -> {
            if ("/hello".equals(msg)) {
                context.bus().publish("\uD83D\uDC4B");
            }
        });
    }

    @Override
    public void start() {
        // nothing
    }

    @Override
    public void stop() {
        // nothing
    }
}
