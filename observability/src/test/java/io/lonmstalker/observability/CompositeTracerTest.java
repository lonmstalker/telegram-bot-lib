/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.observability;

import static io.lonmstalker.tgkit.observability.Tags.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.observability.impl.CompositeTracer;
import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tag;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InOrder;

@SuppressWarnings("all")
class CompositeTracerTest {

  Tracer t1 = mock(Tracer.class);
  Tracer t2 = mock(Tracer.class);
  Span s1 = mock(Span.class);
  Span s2 = mock(Span.class);

  @BeforeEach
  void stubs() {
    when(t1.start("op", of(Tag.of("1", "1"), Tag.of("2", "2")))).thenReturn(s1);
    when(t2.start("op", of(Tag.of("1", "1"), Tag.of("2", "2")))).thenReturn(s2);
  }

  @Test
  void delegatesStartAndClose() {
    new CompositeTracer(List.of(t1, t2)).start("op", of(Tag.of("1", "1"), Tag.of("2", "2")));
    ArgumentMatcher<Tags> matcher =
        e -> e.items().length == 2 && Arrays.stream(e.items()).anyMatch(t -> t.key().equals("1"));

    InOrder io = inOrder(t1, t2, s1, s2);
    io.verify(t1).start(eq("op"), argThat(matcher));
    io.verify(t2).start(eq("op"), argThat(matcher));
    io.verifyNoMoreInteractions();
  }
}
