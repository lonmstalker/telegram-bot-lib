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

package io.github.tgkit.core.parse_mode;

import org.checkerframework.checker.nullness.qual.NonNull;

public enum ParseMode {
  NONE("None"),

  // экранируются & < > " ' /
  HTML(org.telegram.telegrambots.meta.api.methods.ParseMode.HTML),

  // экранируются `_ * [` ]
  MARKDOWN(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN),

  // экранируются _*[]()~\>#+-=|{}.!
  MARKDOWN_V2(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2);

  private final String mode;

  ParseMode(String mode) {
    this.mode = mode;
  }

  public static @NonNull ParseMode byMode(@NonNull String mode) {
    return switch (mode) {
      case org.telegram.telegrambots.meta.api.methods.ParseMode.HTML -> HTML;
      case org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN -> MARKDOWN;
      case org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2 -> MARKDOWN_V2;
      case "None" -> NONE;
      default -> throw new IllegalArgumentException("Unknown mode: " + mode);
    };
  }

  public String getMode() {
    return mode;
  }
}
