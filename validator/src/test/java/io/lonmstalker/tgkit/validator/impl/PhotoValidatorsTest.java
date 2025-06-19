package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import java.util.List;

import static io.lonmstalker.tgkit.validator.impl.PhotoValidators.*;
import static org.junit.jupiter.api.Assertions.*;

class PhotoValidatorsTest {

    private PhotoSize photo(int sizeKb, int w, int h) {
        PhotoSize p = new PhotoSize();
        p.setFileSize(sizeKb * 1024);
        p.setWidth(w);
        p.setHeight(h);
        p.setFileId("id");
        return p;
    }

    @Test
    void maxSizeKb_allowsWithinLimit() {
        List<PhotoSize> pics = List.of(photo(100, 50, 50));
        assertDoesNotThrow(() -> maxSizeKb(200).validate(pics));
    }

    @Test
    void maxSizeKb_rejectsTooLarge() {
        List<PhotoSize> pics = List.of(photo(300, 50, 50));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> maxSizeKb(200).validate(pics)
        );
        assertEquals("error.photo.tooLarge", ex.getErrorKey().key());
    }

    @Test
    void minResolution_allowsAboveThreshold() {
        List<PhotoSize> pics = List.of(photo(50, 400, 300));
        assertDoesNotThrow(() -> minResolution(200, 200).validate(pics));
    }

    @Test
    void minResolution_rejectsBelowThreshold() {
        List<PhotoSize> pics = List.of(photo(50, 100, 100));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> minResolution(200, 200).validate(pics)
        );
        assertEquals("error.photo.resolution", ex.getErrorKey().key());
    }

    @Test
    void safeSearch_allowsWhenServiceUnavailable() {
        // если ContentModerationService == null, валидатор пропускает
        List<PhotoSize> pics = List.of(photo(10, 10, 10));
        assertDoesNotThrow(() -> safeSearch().validate(pics));
    }
}
