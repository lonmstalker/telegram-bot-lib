package io.lonmstalker.tgkit.core.dsl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;

class MediaGroupChunkingTest {

  static {
    BotCoreInitializer.init();
  }

  @Test
  void splitsIntoChunksOf10() throws IOException {
    TelegramSender sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<SendMediaGroup>any());

    DSLContext ctx = MockCtx.ctx(111L, 222L, sender);

    Path tmp = Files.createTempFile("img", ".jpg");
    InputFile f = new InputFile(tmp.toFile(), "img.jpg");

    MediaGroupBuilder b = new MediaGroupBuilder(ctx);
    for (int i = 0; i < 17; i++) {
      b.photo(f, "cap");
    }
    b.send();

    // Проверяем, сколько раз вызван execute
    verify(sender, times(2)).execute(any(SendMediaGroup.class));
  }
}
