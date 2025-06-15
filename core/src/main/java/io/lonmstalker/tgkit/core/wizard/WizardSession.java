package io.lonmstalker.tgkit.core.wizard;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class WizardSession {
    @Setter
    private int stepIdx;
    private final Map<String, String> data = new HashMap<>();
}
