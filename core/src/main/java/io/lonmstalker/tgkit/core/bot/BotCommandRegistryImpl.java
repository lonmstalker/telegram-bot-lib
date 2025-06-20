package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

/** Потокобезопасная реализация реестра команд бота. */
public final class BotCommandRegistryImpl implements BotCommandRegistry {

  private final CopyOnWriteArrayList<BotCommand<?>> commands = new CopyOnWriteArrayList<>();

  @Override
  public void add(@NonNull BotCommand<?> command) {
    commands.add(command);
    commands.sort(Comparator.comparingInt(BotCommand::order));
  }

  @Override
  @SuppressWarnings("unchecked")
  public <T extends BotApiObject> BotCommand<T> find(
      @NonNull BotRequestType type, @NonNull String botGroup, @NonNull T data) {
    for (BotCommand<?> rawCmd : commands) {
      var supportedType = rawCmd.type() == BotRequestType.ANY || rawCmd.type() == type;
      if (supportedType
          && (StringUtils.isEmpty(rawCmd.botGroup()) || rawCmd.botGroup().equals(botGroup))) {
        CommandMatch<T> matcher = (CommandMatch<T>) rawCmd.matcher();
        if (matcher.match(data)) {
          return (BotCommand<T>) rawCmd;
        }
      }
    }
    return null;
  }

  @Override
  public @NonNull List<BotCommand<?>> all() {
    return Collections.unmodifiableList(commands);
  }
}
