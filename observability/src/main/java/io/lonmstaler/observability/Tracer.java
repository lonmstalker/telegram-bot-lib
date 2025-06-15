package io.lonmstaler.observability;

import io.opentelemetry.api.common.Attributes;

public interface Tracer {
    Span start(String spanName, Attributes attributes);
}
