package io.lonmstalker.tgkit.validator.impl;

import static io.lonmstalker.tgkit.validator.impl.VideoValidators.*;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.ValidationException;
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
