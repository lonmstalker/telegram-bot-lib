/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.core.dsl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import io.github.tgkit.core.bot.TelegramSender;
import io.github.tgkit.core.dsl.common.MockCtx;
import io.github.tgkit.core.dsl.context.DSLContext;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.objects.InputFile;

class MediaGroupChunkingTest {

  static {
    TestBotBootstrap.initOnce();
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
