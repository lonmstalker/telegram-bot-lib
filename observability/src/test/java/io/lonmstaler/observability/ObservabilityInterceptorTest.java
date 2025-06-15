package io.lonmstaler.observability;

import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.observability.ObservabilityInterceptor;
import io.lonmstalker.observability.Span;
import io.lonmstalker.observability.Tracer;
import io.lonmstalker.tgkit.core.utils.UpdateUtils;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import io.micrometer.core.instrument.Tag;
import io.opentelemetry.api.common.Attributes;
import org.mockito.ArgumentCaptor;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ObservabilityInterceptorTest {
    private final MeterRegistry registry = new SimpleMeterRegistry();
    private MetricsCollector metrics;
    private Tracer tracer;
    private Counter counter;
    private Span span;
    private ObservabilityInterceptor interceptor;

    @AfterEach
    void clear() {
        MDC.clear();
    }

    @BeforeEach
    void init() {
        metrics = mock(MetricsCollector.class);
        tracer = mock(Tracer.class);
        Timer timer = mock(Timer.class);
        counter = mock(Counter.class);
        span = mock(Span.class);

        when(metrics.registry()).thenReturn(registry);
        when(metrics.timer(anyString(), any())).thenReturn(timer);
        when(metrics.counter(anyString(), any())).thenReturn(counter);
        when(tracer.start(anyString(), any(Attributes.class))).thenReturn(span);

        interceptor = new ObservabilityInterceptor(metrics, tracer);
    }

    private Update createUpdate(int id) {
        Update update = new Update();
        update.setUpdateId(id);
        update.setMessage(new Message());
        return update;
    }

    @Test
    void span_and_metrics_on_success() {
        Update update = createUpdate(1);
        interceptor.preHandle(update);
        assertEquals("1", MDC.get("updateId"));
        interceptor.afterCompletion(update, null, null);
        verify(tracer).start(eq("update"), any(Attributes.class));
        verify(span).close();

        ArgumentCaptor<Tags> captor = ArgumentCaptor.forClass(Tags.class);
        verify(metrics).counter(eq("updates_total"), captor.capture());
        Tag tag = captor.getValue().items()[0];
        assertEquals("type", tag.getKey());
        assertEquals(UpdateUtils.getType(update).name(), tag.getValue());

        verify(metrics).timer(eq("update_latency_ms"), any());
        assertNull(MDC.get("updateId"));
    }

    @Test
    void error_metrics_and_span() {
        Update update = createUpdate(2);
        interceptor.preHandle(update);
        RuntimeException ex = new RuntimeException("boom");
        interceptor.afterCompletion(update, null, ex);
        verify(span).setError(ex);
        verify(counter, atLeastOnce()).increment();

        ArgumentCaptor<Tags> captor = ArgumentCaptor.forClass(Tags.class);
        verify(metrics).counter(eq("errors_total"), captor.capture());
        Tag tag = captor.getValue().items()[0];
        assertEquals("type", tag.getKey());
        assertEquals(UpdateUtils.getType(update).name(), tag.getValue());

        verify(metrics).timer(eq("update_latency_ms"), any());
    }
}
