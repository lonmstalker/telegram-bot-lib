/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lonmstalker.tgkit.core.matching;

import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Проверяет текст сообщения по регулярному выражению.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * CommandMatch<Message> match = new MessageRegexMatch("\\d+");
 * }</pre>
 */
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
