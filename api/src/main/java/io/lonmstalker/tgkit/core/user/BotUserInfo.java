package io.lonmstalker.tgkit.core.user;

import java.util.Locale;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface BotUserInfo {

  @Nullable Long chatId();

  @Nullable Long userId();

  @Nullable Long internalUserId();

  @NonNull Set<String> roles();

  default @Nullable Locale locale() {
    return null;
  }
}
