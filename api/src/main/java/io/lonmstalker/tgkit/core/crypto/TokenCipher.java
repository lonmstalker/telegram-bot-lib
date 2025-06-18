package io.lonmstalker.tgkit.core.crypto;

import org.checkerframework.checker.nullness.qual.NonNull;

public interface TokenCipher {
    @NonNull String encrypt(@NonNull String token);
    @NonNull String decrypt(@NonNull String cipherText);
}
