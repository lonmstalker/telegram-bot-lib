package io.lonmstalker.tgkit.core.wizard;

public record StepMeta(
        int order,
        String askKey,
        String defaultAsk,
        String saveKey,
        StepValidator validator
) {}
