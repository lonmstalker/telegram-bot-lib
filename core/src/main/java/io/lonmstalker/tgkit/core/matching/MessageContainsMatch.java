package io.lonmstalker.tgkit.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/** Matcher that checks if a message text contains a substring. */
public class MessageContainsMatch implements CommandMatch<Message> {

  private final String text;
  private final boolean ignoreCase;

  public MessageContainsMatch(@NonNull String text) {
    this(text, false);
  }

  public MessageContainsMatch(@NonNull String text, boolean ignoreCase) {
    this.text = text;
    this.ignoreCase = ignoreCase;
  }

  @Override
  public boolean match(@NonNull Message data) {
    if (data.getText() == null) {
      return false;
    }
    if (ignoreCase) {
      return data.getText().toLowerCase().contains(text.toLowerCase());
    }
    return data.getText().contains(text);
  }
}
