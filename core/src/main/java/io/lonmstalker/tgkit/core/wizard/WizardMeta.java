package io.lonmstalker.tgkit.core.wizard;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

public record WizardMeta(@NonNull String id, @NonNull List<StepMeta> steps) {}
