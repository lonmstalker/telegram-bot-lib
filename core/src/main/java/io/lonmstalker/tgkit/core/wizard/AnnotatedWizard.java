package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.wizard.annotation.Step;
import io.lonmstalker.tgkit.core.wizard.annotation.Wizard;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/** Утилита для построения {@link WizardMeta} из аннотированного класса. */
public final class AnnotatedWizard {

    private AnnotatedWizard() {
    }

    public static WizardMeta parse(Class<?> clazz) {
        Wizard w = clazz.getAnnotation(Wizard.class);
        if (w == null) {
            throw new IllegalArgumentException("Missing @Wizard: " + clazz);
        }
        List<StepMeta> steps = new ArrayList<>();
        for (Method m : clazz.getDeclaredMethods()) {
            Step step = m.getAnnotation(Step.class);
            if (step != null) {
                StepValidator validator;
                try {
                    validator = step.validator().getDeclaredConstructor().newInstance();
                } catch (Exception e) {
                    validator = new StepValidator.Identity();
                }
                steps.add(new StepMeta(step.order(), step.askKey(), step.defaultAsk(), step.saveKey(), validator));
            }
        }
        steps.sort(Comparator.comparingInt(StepMeta::order));
        return new WizardMeta(w.id(), steps);
    }
}
