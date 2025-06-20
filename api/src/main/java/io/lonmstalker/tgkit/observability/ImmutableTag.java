package io.lonmstalker.tgkit.observability;

import org.checkerframework.checker.nullness.qual.NonNull;

public record ImmutableTag(String key, String value) implements Tag {

  public ImmutableTag(@NonNull String key, @NonNull String value) {
    this.key = key;
    this.value = value;
  }
}
