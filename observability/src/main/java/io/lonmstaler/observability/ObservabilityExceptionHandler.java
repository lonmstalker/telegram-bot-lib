package io.lonmstaler.observability;

import io.lonmstalker.core.exception.BotExceptionHandler;
import io.micrometer.core.instrument.Counter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ObservabilityExceptionHandler implements BotExceptionHandler {
    private final BotExceptionHandler delegate;
    private final Counter errors;

    public ObservabilityExceptionHandler(BotExceptionHandler delegate, MetricsCollector collector) {
        this.delegate = delegate != null ? delegate : (u, e) -> {};
        this.errors = collector.counter("errors_total", Tags.of());
    }

    @Override
    public void handle(@NonNull Update update, @NonNull Exception ex) {
        errors.increment();
        delegate.handle(update, ex);
    }
}
