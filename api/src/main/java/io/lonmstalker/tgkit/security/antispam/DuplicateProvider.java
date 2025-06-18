package io.lonmstalker.tgkit.security.antispam;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface DuplicateProvider {

    /**
     * @return {@code true}, если такой текст уже встречался в окне TTL
     */
    boolean isDuplicate(long chat, @NonNull String text);
}
