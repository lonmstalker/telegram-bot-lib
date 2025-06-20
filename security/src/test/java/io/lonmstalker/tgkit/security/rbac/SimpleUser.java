package io.lonmstalker.tgkit.security.rbac;

import io.lonmstalker.tgkit.core.user.BotUserInfo;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.Nullable;

/* helper: минимальная имплементация BotUserInfo */
record SimpleUser(Set<String> roles) implements BotUserInfo {

  public Long chatId() {
    return 1L;
  }

  public Long userId() {
    return 1L;
  }

  @Override
  public @Nullable Long internalUserId() {
    return 0L;
  }
}
