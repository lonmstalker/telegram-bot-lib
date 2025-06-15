package io.craftbot.security.spi;

import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;

public record CaptchaChallenge(String message,
                               InputFile photo,
                               ReplyKeyboard keyboard,
                               int ttlSeconds) {}
