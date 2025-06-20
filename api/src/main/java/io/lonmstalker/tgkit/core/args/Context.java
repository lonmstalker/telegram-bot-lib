package io.lonmstalker.tgkit.core.args;

import java.util.regex.Matcher;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public record Context<T>(@NonNull T data, @Nullable Matcher matcher) {}
