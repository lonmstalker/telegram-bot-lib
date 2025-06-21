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

package io.github.tgkit.security.limiter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

import io.github.tgkit.core.interceptor.BotInterceptor;
import io.github.tgkit.security.*;
import io.github.tgkit.security.init.BotSecurityInitializer;
import io.github.tgkit.security.ratelimit.*;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.lang.reflect.Method;
import java.util.Optional;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import org.mockito.Mockito;

class TelegramSenderRateLimiterTest {

  static {
    TestBotBootstrap.initOnce();
    BotSecurityInitializer.init();
  }

  Method handler;

  RateLimiter backend;
  RateLimitBotCommandFactory factory;

  /* === dummy handler method with annotations ==================== */
  @RateLimit(key = LimiterKey.USER, permits = 2, seconds = 60)
  @RateLimit(key = LimiterKey.GLOBAL, permits = 5, seconds = 60)
  void roll() {
  }

  @BeforeEach
  void init() throws Exception {
    handler = getClass().getDeclaredMethod("roll");
    backend = mock(RateLimiter.class);
    factory = new RateLimitBotCommandFactory();
  }

  /* -------------------------------------------------------------- */

  @Test
  void factoryBuildsInterceptorOnce() {
    RateLimit first = handler.getAnnotationsByType(RateLimit.class)[0];
    Assertions.assertNotNull(first);
    Optional<BotInterceptor> i1 = factory.build(handler, first);

    RateLimit second = handler.getAnnotationsByType(RateLimit.class)[1];
    Assertions.assertNotNull(second);
    Optional<BotInterceptor> i2 = factory.build(handler, second);

    assertThat(i1).isPresent();
    assertThat(i2).isPresent();
    assertThat(i1.get()).isInstanceOf(RateLimitInterceptor.class);
    // two distinct instances (factory not caching – allowed)
    assertThat(i1.get()).isNotSameAs(i2.get());
  }

  @Test
  void interceptorGeneratesCorrectKeys() {
    when(backend.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true);

    RateLimit first = handler.getAnnotationsByType(RateLimit.class)[0];
    Assertions.assertNotNull(first);
    Optional<BotInterceptor> i = factory.build(handler, first);

    i.orElseThrow().preHandle(TestUtils.message(55, 9), Mockito.mock());

    InOrder io = inOrder(backend);
    io.verify(backend).tryAcquire(eq("cmd:roll:user:9"), eq(2), eq(60));
    io.verify(backend).tryAcquire(eq("cmd:roll:global"), eq(5), eq(60));
    io.verifyNoMoreInteractions();
  }

  @Test
  void interceptorThrowsWhenLimitExceeded() {
    when(backend.tryAcquire(anyString(), anyInt(), anyInt()))
        .thenReturn(false); // first limit fails

    RateLimit first = handler.getAnnotationsByType(RateLimit.class)[0];
    Assertions.assertNotNull(first);
    Optional<BotInterceptor> i = factory.apply(handler, first);

    assertThatThrownBy(() -> i.orElseThrow().preHandle(TestUtils.message(77, 3), Mockito.mock()))
        .isInstanceOf(RateLimitInterceptor.RateLimitExceededException.class);
  }

  @Test
  void interceptorPassesWhenBackendOk() {
    when(backend.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true);

    RateLimit first = handler.getAnnotationsByType(RateLimit.class)[0];
    Assertions.assertNotNull(first);
    Optional<BotInterceptor> i = factory.build(handler, first);

    // no exception
    i.orElseThrow().preHandle(TestUtils.message(99, 42), Mockito.mock());
  }
}
