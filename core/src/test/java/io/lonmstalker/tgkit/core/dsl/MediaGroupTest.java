package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/** Проверка отправки медиа-групп. */
public class MediaGroupTest {
    @Test
    void chunking() {
        FakeTransport tg = new FakeTransport();
        MediaGroupBuilder b = BotResponse.mediaGroup().chat(1);
        IntStream.range(0, 12).forEach(i -> b.photo(new InputFile("f" + i), "c"));
        b.send(tg);
        assertThat(tg.sent).hasSize(2);
    }
}
