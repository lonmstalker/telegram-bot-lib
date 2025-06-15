package io.lonmstalker.tgkit.core.wizard;

import java.util.HashMap;
import java.util.Map;

public class WizardSession {
    private int stepIdx;
    private final Map<String, String> data = new HashMap<>();

    public int getStepIdx() {
        return stepIdx;
    }

    public void setStepIdx(int stepIdx) {
        this.stepIdx = stepIdx;
    }

    public Map<String, String> getData() {
        return data;
    }
}
