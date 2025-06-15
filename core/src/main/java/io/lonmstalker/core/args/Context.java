package io.lonmstalker.core.args;

import io.lonmstalker.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.regex.Matcher;

public record Context(@NonNull BotRequest<?> request,
                      Matcher matcher) {
}
