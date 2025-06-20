package io.lonmstalker.tgkit.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/** Matcher that checks message text equality. */
public class MessageTextMatch implements CommandMatch<Message> {

  private final String text;
  private final boolean ignoreCase;

  public MessageTextMatch(@NonNull String text) {
    this(text, false);
  }

  public MessageTextMatch(@NonNull String text, boolean ignoreCase) {
    this.text = text;
    this.ignoreCase = ignoreCase;
  }

  @Override
  public boolean match(@NonNull Message data) {
    if (data.getText() == null) {
      return false;
    }
    if (ignoreCase) {
      return text.equalsIgnoreCase(data.getText());
    }
    return text.equals(data.getText());
  }
}
