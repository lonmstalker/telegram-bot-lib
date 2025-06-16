package io.lonmstalker.observability.impl;

import io.lonmstalker.observability.Span;
import io.lonmstalker.observability.Tags;
import io.lonmstalker.observability.Tracer;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.List;

/**
 * Трейсер, объединяющий сразу несколько Tracer-реализаций.
 */
public class CompositeTracer implements Tracer {
    private final List<Tracer> delegates;

    public CompositeTracer(@NonNull List<Tracer> delegates) {
        this.delegates = List.copyOf(delegates);
    }

    @Override
    public Span start(@NonNull String name, @NonNull Tags tags) {
        List<Span> spans = delegates.stream()
                .map(t -> t.start(name, tags))
                .toList();
        return new CompositeSpan(spans);
    }

    /**
     * CompositeSpan: делегирует операции всем вложенным спанам.
     */
    private record CompositeSpan(List<Span> spans) implements Span {
        private CompositeSpan(@NonNull List<Span> spans) {
            this.spans = spans;
        }

        @Override
        public void setError(@NonNull Throwable error) {
            spans.forEach(s -> s.setError(error));
        }

        @Override
        public void setTag(@NonNull String key, @NonNull String value) {
            spans.forEach(s -> s.setTag(key, value));
        }

        @Override
        public void close() {
            // Закрываем в обратном порядке, на случай вложенных скоупов
            for (int i = spans.size() - 1; i >= 0; i--) {
                spans.get(i).close();
            }
        }
    }
}
