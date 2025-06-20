package io.lonmstalker.observability;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.RouteContextHolder;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistry;
import io.lonmstalker.tgkit.core.bot.BotCommandRegistryImpl;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotRegistryImpl;
import io.lonmstalker.tgkit.core.bot.BotState;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.storage.BotRequestContextHolder;
import io.lonmstalker.tgkit.observability.Span;
import io.lonmstalker.tgkit.observability.Tags;
import io.lonmstalker.tgkit.observability.Tracer;
import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@SuppressWarnings("all")
public class BotAdapterCleanupTest {
  private MetricsCollector metrics;
  private Tracer tracer;
  private Span span;

  @BeforeAll
  static void initCore() {
    BotCoreInitializer.init();
  }

  @BeforeEach
  void init() {
    metrics = mock(MetricsCollector.class);
    tracer = mock(Tracer.class);
    MeterRegistry registry = new SimpleMeterRegistry();
    when(metrics.registry()).thenReturn(registry);
    when(metrics.timer(anyString(), any())).thenReturn(mock(Timer.class));
    when(metrics.counter(anyString(), any())).thenReturn(mock(Counter.class));
    span = mock(Span.class);
    when(tracer.start(anyString(), any(Tags.class))).thenReturn(span);
  }

  @AfterEach
  void clear() {
    MDC.clear();
  }

  @Test
  void clearsThreadLocalsWhenAfterCompletionFails() throws Exception {
    ObservabilityInterceptor obs = new ObservabilityInterceptor(metrics, tracer);
    BotInterceptor failing =
        new BotInterceptor() {
          @Override
          public void preHandle(@NonNull Update u, @NonNull BotRequest<?> r) {}

          @Override
          public void postHandle(@NonNull Update u, @NonNull BotRequest<?> r) {}

          @Override
          public void afterCompletion(
              @NonNull Update u,
              @Nullable BotRequest<?> req,
              @Nullable BotResponse resp,
              @Nullable Exception ex) {
            throw new RuntimeException("after");
          }
        };

    BotCommand<Message> command = mock(BotCommand.class);
    when(command.type()).thenReturn(BotRequestType.MESSAGE);
    when(command.matcher()).thenReturn(m -> true);
    when(command.interceptors()).thenReturn(new ArrayList<>());
    when(command.handle(any())).thenThrow(new RuntimeException("boom"));

    BotCommandRegistry reg = new BotCommandRegistryImpl();
    reg.add(command);

    BotConfig cfg = BotConfig.builder().baseUrl("http://localhost").build();
    TelegramSender sender = new TelegramSender(cfg, "TOKEN");

    BotAdapterImpl adapter =
        BotAdapterImpl.builder()
            .internalId(1L)
            .config(cfg)
            .sender(sender)
            .registry(reg)
            .interceptors(List.of(failing, obs))
            .build();

    Bot bot = mock(Bot.class);
    when(bot.state()).thenReturn(BotState.RUNNING);
    when(bot.registry()).thenReturn(reg);
    when(bot.config()).thenReturn(cfg);
    when(bot.token()).thenReturn("TOKEN");
    when(bot.internalId()).thenReturn(1L);
    when(bot.externalId()).thenReturn(0L);
    when(bot.username()).thenReturn("bot");
    when(bot.botRegistry()).thenReturn(BotRegistryImpl.getInstance());
    adapter.setCurrentBot(bot);

    Update update = new Update();
    Message msg = new Message();
    msg.setText("hi");
    msg.setMessageId(1);
    update.setMessage(msg);
    update.setUpdateId(1);

    RuntimeException ex = assertThrows(RuntimeException.class, () -> adapter.handle(update));
    assertEquals("boom", ex.getMessage());
    assertEquals(1, ex.getSuppressed().length);
    assertEquals("after", ex.getSuppressed()[0].getMessage());

    assertNull(BotRequestContextHolder.getUpdate());
    assertNull(RouteContextHolder.getMatcher());

    Field fSpan = ObservabilityInterceptor.class.getDeclaredField("SPANS");
    fSpan.setAccessible(true);
    ThreadLocal<?> spans = (ThreadLocal<?>) fSpan.get(obs);
    assertNull(spans.get());

    Field fSample = ObservabilityInterceptor.class.getDeclaredField("SAMPLE");
    fSample.setAccessible(true);
    ThreadLocal<?> samples = (ThreadLocal<?>) fSample.get(obs);
    assertNull(samples.get());

    assertNull(MDC.get("updateId"));
    sender.close();
  }
}
