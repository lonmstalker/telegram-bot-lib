package io.lonmstalker.examples.simplebot;

import static io.lonmstalker.tgkit.core.update.UpdateUtils.resolveChatId;
import static io.lonmstalker.tgkit.core.update.UpdateUtils.resolveUserId;

import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SimpleRoleProvider implements BotUserProvider {

  @Override
  public @NonNull BotUserInfo resolve(@NonNull Update update) {
    var userId = resolveUserId(update);
    var chatId = resolveChatId(update);
    return new SimpleInfo(chatId, userId, null, Set.of());
  }

  private record SimpleInfo(
      @Nullable Long chatId,
      @Nullable Long userId,
      @Nullable Long internalUserId,
      Set<String> roles)
      implements BotUserInfo {}
}
