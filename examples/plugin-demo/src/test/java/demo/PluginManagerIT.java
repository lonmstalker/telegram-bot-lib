package demo;

import static org.junit.jupiter.api.Assertions.assertEquals;

import io.lonmstalker.tgkit.plugin.DefaultEventBus;
import io.lonmstalker.tgkit.plugin.PluginManager;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class PluginManagerIT {
    @TempDir
    Path tempDir;

    @Test
    public void shouldReloadPluginWhenUpdated() throws Exception {
        // build jar from compiled plugin classes
        Path classes = Path.of("target/classes");
        Path jar = tempDir.resolve("greeter.jar");
        JarHelper.buildJar(classes, jar);

        DefaultEventBus bus = new DefaultEventBus();
        RecordingHandler recorder = new RecordingHandler();
        bus.subscribe(recorder);
        PluginManager manager = new PluginManager(tempDir, bus);
        manager.load(jar);
        bus.publish("/hello");
        assertEquals("\uD83D\uDC4B", recorder.message);

        // modify manifest version and rebuild jar
        Path manifest = classes.resolve("tgkit-plugin.yaml");
        Files.writeString(manifest, "id: greeter\nversion: 1.1.0\n");
        JarHelper.buildJar(classes, jar);
        manager.reload("greeter");
        assertEquals("1.1.0", manager.getManifest("greeter").getVersion());

        // restore manifest for cleanliness
        Files.writeString(manifest, "id: greeter\nversion: 1.0.0\n");
    }

    private static class RecordingHandler implements io.lonmstalker.tgkit.plugin.EventBus.MessageHandler {
        String message;
        @Override
        public void onMessage(String message) { this.message = message; }
    }
}
