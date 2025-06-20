package io.lonmstalker.tgkit.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

/** Matcher that always returns {@code true}. */
public class AlwaysMatch<T extends BotApiObject> implements CommandMatch<T> {

  @Override
  public boolean match(@NonNull T data) {
    return true;
  }
}
