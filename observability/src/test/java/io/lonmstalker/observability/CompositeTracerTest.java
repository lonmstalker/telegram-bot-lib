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
