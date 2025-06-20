package io.lonmstalker.tgkit.core.matching;

import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/** Matcher that checks a message text against a regular expression. */
public class MessageRegexMatch implements CommandMatch<Message> {

  private final Pattern pattern;

  public MessageRegexMatch(@NonNull String regex) {
    this.pattern = Pattern.compile(regex);
  }

  public MessageRegexMatch(@NonNull Pattern pattern) {
    this.pattern = pattern;
  }

  @Override
  public boolean match(@NonNull Message data) {
    if (data.getText() == null) {
      return false;
    }
    Matcher m = pattern.matcher(data.getText());
    boolean res = m.matches();
    if (res) {
      // проставляет matcher для возможности извлечения аргументов из текста
      RouteContextHolder.setMatcher(m);
    }
    return res;
  }
}
