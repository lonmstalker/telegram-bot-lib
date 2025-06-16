package io.lonmstalker.tgkit.core.wizard;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;

public class WizardBuilder {
    private final String id;
    private final List<StepMeta> steps = new ArrayList<>();

    public WizardBuilder(@NonNull String id) {
        this.id = id;
    }

    private int counter = 0;

    public WizardBuilder step(@NonNull String askKey,
                              @NonNull String defaultAsk,
                              @NonNull String saveKey,
                              @NonNull StepValidator validator) {
        steps.add(new StepMeta(counter++, askKey, defaultAsk, saveKey, validator));
        return this;
    }

    public WizardMeta build() {
        return new WizardMeta(id, List.copyOf(steps));
    }
}
