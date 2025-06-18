package io.lonmstalker.tgkit.security.secret;


import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;

public interface SecretStore extends AutoCloseable {

    /**
     * @return secret or {@code Optional.empty()} if provider does not have it.
     */
    Optional<String> get(@NonNull String key);

    /**
     * override if store keeps connections/resources
     */
    default void close() { /* noop */ }
}