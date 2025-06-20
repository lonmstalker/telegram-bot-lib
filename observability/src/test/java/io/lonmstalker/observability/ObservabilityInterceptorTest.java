package io.lonmstalker.observability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.MDC;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@SuppressWarnings("all")
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
    when(tracer.start(anyString(), any(Tags.class))).thenReturn(span);

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
    interceptor.preHandle(update, Mockito.mock());
    assertEquals("1", MDC.get("updateId"));
    interceptor.afterCompletion(update, Mockito.mock(), null, null);
    ArgumentCaptor<Tags> tagsCaptor = ArgumentCaptor.forClass(Tags.class);
    verify(tracer).start(eq("update"), any(Tags.class));
    verify(span).close();
    verify(metrics).timer(eq("update_latency_ms"), tagsCaptor.capture());
    verify(metrics, atLeastOnce()).counter(eq("updates_total"), any());
    Tags tags = tagsCaptor.getValue();
    assertNotNull(tags);
    assertEquals("type", tags.items()[0].key());
    assertEquals("MESSAGE", tags.items()[0].value());
    assertNull(MDC.get("updateId"));
  }

  @Test
  void error_metrics_and_span() {
    Update update = createUpdate(2);
    interceptor.preHandle(update, Mockito.mock());
    RuntimeException ex = new RuntimeException("boom");
    interceptor.afterCompletion(update, Mockito.mock(), null, ex);
    ArgumentCaptor<Tags> tagsCaptor = ArgumentCaptor.forClass(Tags.class);
    verify(span).setError(ex);
    verify(counter, atLeastOnce()).increment();
    verify(metrics).timer(eq("update_latency_ms"), tagsCaptor.capture());
    Tags tags = tagsCaptor.getValue();
    assertEquals("MESSAGE", tags.items()[0].value());
  }
}
