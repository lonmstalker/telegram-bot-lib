package io.lonmstalker.tgkit.core.args;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.regex.Matcher;

public record Context<T>(@NonNull T data,
                         @Nullable Matcher matcher) {
}
