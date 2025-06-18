package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/* File size in bytes */
public final class FileSizeValidator implements Validator<InputFile> {
    private final long maxBytes;

    public FileSizeValidator(long maxBytes) { this.maxBytes = maxBytes; }
    @Override public void validate(InputFile f) {
        if (f.getNewMediaFile() != null && f.getNewMediaFile().length() > maxBytes)
            throw new BotApiException("File exceeds " + maxBytes/1024/1024 + " MB");
    }
}
