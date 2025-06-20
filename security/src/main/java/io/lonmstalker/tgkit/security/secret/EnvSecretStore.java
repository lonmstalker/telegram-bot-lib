package io.lonmstalker.tgkit.security.secret;

import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class EnvSecretStore implements SecretStore {

  @Override
  public Optional<String> get(@NonNull String key) {
    return Optional.ofNullable(System.getenv(key));
  }
}
