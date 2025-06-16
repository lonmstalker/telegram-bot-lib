package io.lonmstalker.tgkit.core.wizard;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public record StepMeta(
        int order,
        @NonNull String askKey,
        @Nullable String defaultAsk,
        @NonNull String saveKey,
        @Nullable StepValidator validator
) {}
