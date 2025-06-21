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
package io.github.tgkit.validator.impl;

import static io.github.tgkit.validator.impl.VideoValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.github.tgkit.internal.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Video;

class VideoValidatorsTest {

  private Video video(int sizeKb, int durationSec) {
    Video v = new Video();
    v.setFileSize(sizeKb * 1024L);
    v.setDuration(durationSec);
    v.setFileId("vid");
    return v;
  }

  @Test
  void maxSizeKb_allowsWithinLimit() {
    assertDoesNotThrow(() -> maxSizeKb(500).validate(video(400, 10)));
  }

  @Test
  void maxSizeKb_rejectsTooLarge() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> maxSizeKb(500).validate(video(600, 10)));
    assertEquals("error.video.tooLarge", ex.getErrorKey().key());
  }

  @Test
  void maxDurationSec_allowsWithinLimit() {
    assertDoesNotThrow(() -> maxDurationSec(120).validate(video(100, 100)));
  }

  @Test
  void maxDurationSec_rejectsTooLong() {
    ValidationException ex =
        assertThrows(ValidationException.class, () -> maxDurationSec(60).validate(video(100, 120)));
    assertEquals("error.video.tooLong", ex.getErrorKey().key());
  }

  @Test
  void safeSearch_allowsWhenServiceUnavailable() {
    assertDoesNotThrow(() -> safeSearch().validate(video(10, 1)));
  }
}
