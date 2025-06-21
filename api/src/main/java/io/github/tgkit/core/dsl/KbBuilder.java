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
package io.github.tgkit.core.dsl;

import io.github.tgkit.core.i18n.MessageLocalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/** Конструктор inline-клавиатуры. */
public final class KbBuilder {
  private final @NonNull MessageLocalizer loc;
  private final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

  KbBuilder(@NonNull MessageLocalizer loc) {
    this.loc = loc;
  }

  /** Добавляет строку кнопок. */
  public @NonNull KbBuilder row(@NonNull Button... buttons) {
    rows.add(to(buttons));
    return this;
  }

  /** Каждая кнопка в отдельной строке. */
  public @NonNull KbBuilder col(@NonNull Button... buttons) {
    for (Button b : buttons) {
      rows.add(to(b));
    }
    return this;
  }

  /** Добавляет строку кнопок. */
  public <T> @NonNull KbBuilder rowFrom(
      @NonNull Collection<T> data, @NonNull Function<T, InlineKeyboardButton> map) {
    List<InlineKeyboardButton> cur = new ArrayList<>();
    for (T elem : data) {
      cur.add(map.apply(elem));
    }
    rows.add(cur);
    return this;
  }

  /** Каждая кнопка в отдельной строке. */
  public <T> @NonNull KbBuilder colFrom(
      @NonNull Collection<T> data, @NonNull Function<T, InlineKeyboardButton> map) {
    for (T elem : data) {
      rows.add(List.of(map.apply(elem)));
    }
    return this;
  }

  /** Размещает кнопки по сетке. */
  public @NonNull KbBuilder grid(int cols, @NonNull Button... buttons) {
    List<InlineKeyboardButton> cur = new ArrayList<>();
    for (Button b : buttons) {
      cur.add(b.build(loc));
      if (cur.size() == cols) {
        rows.add(cur);
        cur = new ArrayList<>();
      }
    }
    if (!cur.isEmpty()) {
      rows.add(cur);
    }
    return this;
  }

  /** Итоговая разметка. */
  public @NonNull InlineKeyboardMarkup build() {
    return new InlineKeyboardMarkup(rows);
  }

  @NonNull List<List<InlineKeyboardButton>> rows() {
    return rows;
  }

  private @NonNull List<InlineKeyboardButton> to(@NonNull Button... buttons) {
    return Arrays.stream(buttons).map(b -> b.build(loc)).collect(Collectors.toList());
  }

  private @NonNull List<InlineKeyboardButton> to(@NonNull Button b) {
    return List.of(b.build(loc));
  }
}
