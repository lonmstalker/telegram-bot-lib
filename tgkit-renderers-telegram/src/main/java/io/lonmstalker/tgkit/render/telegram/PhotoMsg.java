package io.lonmstalker.tgkit.render.telegram;

import org.telegram.telegrambots.meta.api.objects.InputFile;

public record PhotoMsg(InputFile file, String caption) {}
