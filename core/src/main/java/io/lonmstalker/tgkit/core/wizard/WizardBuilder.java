package io.lonmstalker.tgkit.core.wizard;

import java.util.ArrayList;
import java.util.List;

public class WizardBuilder {
    private final String id;
    private final List<StepMeta> steps = new ArrayList<>();

    public WizardBuilder(String id) {
        this.id = id;
    }

    private int counter = 0;

    public WizardBuilder step(String askKey, String defaultAsk, String saveKey, StepValidator validator) {
        steps.add(new StepMeta(counter++, askKey, defaultAsk, saveKey, validator));
        return this;
    }

    public WizardMeta build() {
        return new WizardMeta(id, List.copyOf(steps));
    }
}
