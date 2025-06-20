package io.lonmstalker.tgkit.security.secret;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface SecretStore extends AutoCloseable {

  /**
   * @return secret or {@code Optional.empty()} if provider does not have it.
   */
  Optional<String> get(@NonNull String key);

  /** override if store keeps connections/resources */
  default void close() {
    /* noop */
  }
}
