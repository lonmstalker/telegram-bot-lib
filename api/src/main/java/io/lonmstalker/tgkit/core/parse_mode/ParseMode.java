package io.lonmstalker.tgkit.core.parse_mode;

import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

public enum ParseMode {
  NONE("None"),

  // экранируются & < > " ' /
  HTML(org.telegram.telegrambots.meta.api.methods.ParseMode.HTML),

  // экранируются `_ * [` ]
  MARKDOWN(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN),

  // экранируются _*[]()~\>#+-=|{}.!
  MARKDOWN_V2(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2);

  @Getter private final String mode;

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
}
