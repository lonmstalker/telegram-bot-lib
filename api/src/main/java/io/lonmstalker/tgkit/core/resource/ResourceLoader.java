package io.lonmstalker.tgkit.core.resource;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.io.IOException;
import java.io.InputStream;

public interface ResourceLoader {

    /**
     * Открывает поток к содержимому.
     */
    @NonNull
    InputStream open() throws IOException;

    /**
     * Идентификатор/имя (используется для логов и чтобы понять расширение).
     */
    @NonNull
    String id();

    /* sugar-helpers */
    default byte[] bytes() throws IOException {
        try (InputStream is = open()) {
            return is.readAllBytes();
        }
    }

    default String text() throws IOException {
        return new String(bytes());
    }
}

