/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.observability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.github.tgkit.observability.Span;
import io.github.tgkit.observability.Tags;
import io.github.tgkit.observability.Tracer;
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
