package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.regex.Matcher;

public record Context(@NonNull BotRequest<?> request,
                      @Nullable Matcher matcher) {
}
