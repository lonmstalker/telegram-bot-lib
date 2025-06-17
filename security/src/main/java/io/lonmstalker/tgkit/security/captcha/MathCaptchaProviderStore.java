package io.lonmstalker.tgkit.security.captcha;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.time.Duration;

public interface MathCaptchaProviderStore {

    void put(long chatId, int answer, @NonNull Duration ttl);

    @Nullable
    Integer pop(long chatId);                          // get + delete
}
