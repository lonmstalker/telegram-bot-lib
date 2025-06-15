package io.lonmstalker.tgkit.core.parse_mode;

import lombok.Getter;

public enum ParseMode {
    HTML(org.telegram.telegrambots.meta.api.methods.ParseMode.HTML),
    MARKDOWN(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWN),
    MARKDOWN_V2(org.telegram.telegrambots.meta.api.methods.ParseMode.MARKDOWNV2);

    @Getter
    private final String mode;

    ParseMode(String mode) {
        this.mode = mode;
    }
}