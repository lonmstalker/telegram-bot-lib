package demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.lonmstalker.tgkit.plugin.DefaultEventBus;
import io.lonmstalker.tgkit.plugin.DefaultPluginContext;
import org.junit.jupiter.api.Test;

public class GreeterPluginTest {
    @Test
    public void testReply() throws Exception {
        DefaultEventBus bus = new DefaultEventBus();
        RecordingHandler recorder = new RecordingHandler();
        bus.subscribe(recorder);
        GreeterPlugin plugin = new GreeterPlugin();
        plugin.init(new DefaultPluginContext(bus));
        plugin.start();
        bus.publish("/hello");
        assertEquals("\uD83D\uDC4B", recorder.message);
    }

    private static class RecordingHandler implements io.lonmstalker.tgkit.plugin.EventBus.MessageHandler {
        String message;
        @Override
        public void onMessage(String message) {
            this.message = message;
        }
    }
}
