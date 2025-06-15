package io.lonmstaler.observability;

import io.micrometer.core.instrument.Tag;

public record Tags(Tag... items) {
    public static Tags of(Tag... items) {
        return new Tags(items);
    }
}
