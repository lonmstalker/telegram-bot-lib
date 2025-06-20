package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/* File size in bytes */
public final class FileSizeValidator implements Validator<InputFile> {
  private final long maxBytes;

  public FileSizeValidator(long maxBytes) {
    this.maxBytes = maxBytes;
  }

  @Override
  public void validate(@Nullable InputFile f) {
    if (f == null) {
      throw new BotApiException("Input file is null");
    }
    if (f.getNewMediaFile() != null && f.getNewMediaFile().length() > maxBytes) {
      throw new BotApiException("File exceeds " + maxBytes / 1024 / 1024 + " MB");
    }
  }
}
