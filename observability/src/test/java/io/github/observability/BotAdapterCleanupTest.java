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

import io.github.tgkit.internal.BotCommand;
import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.BotRequestType;
import io.github.tgkit.internal.BotResponse;
import io.github.tgkit.internal.args.RouteContextHolder;
import io.github.tgkit.internal.bot.Bot;
import io.github.tgkit.internal.bot.BotAdapterImpl;
import io.github.tgkit.internal.bot.BotCommandRegistry;
import io.github.tgkit.internal.bot.BotCommandRegistryImpl;
import io.github.tgkit.internal.bot.BotConfig;
import io.github.tgkit.internal.bot.BotRegistryImpl;
import io.github.tgkit.internal.bot.BotState;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.interceptor.BotInterceptor;
import io.github.tgkit.internal.storage.BotRequestContextHolder;
import io.github.tgkit.observability.Span;
import io.github.tgkit.observability.Tags;
import io.github.tgkit.observability.Tracer;
import io.github.tgkit.testkit.TestBotBootstrap;
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
    TestBotBootstrap.initOnce();
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
