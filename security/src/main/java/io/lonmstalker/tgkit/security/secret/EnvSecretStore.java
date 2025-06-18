package io.lonmstalker.tgkit.security.secret;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public final class EnvSecretStore implements SecretStore {

    @Override
    public Optional<String> get(@NonNull String key) {
        return Optional.ofNullable(System.getenv(key));
    }
}