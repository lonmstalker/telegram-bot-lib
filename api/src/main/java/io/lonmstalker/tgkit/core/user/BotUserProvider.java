package io.lonmstalker.tgkit.core.user;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotUserProvider {
  @NonNull BotUserInfo resolve(@NonNull Update update);
}
