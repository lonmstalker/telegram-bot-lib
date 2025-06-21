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
package io.github.tgkit.security.antispam;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizerImpl;
import io.github.tgkit.security.captcha.CaptchaProvider;
import io.github.tgkit.security.init.BotSecurityInitializer;
import io.github.tgkit.security.ratelimit.RateLimiter;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.*;
import org.mockito.InOrder;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.*;

@DisplayName("AntiSpam-interceptor – unit tests")
class AntiSpamInterceptorTest implements WithAssertions {

  DuplicateProvider dup;
  RateLimiter limiter;
  CaptchaProvider captcha;
  BotService bot;

  AntiSpamInterceptor isp;

  static {
    TestBotBootstrap.initOnce();
    BotSecurityInitializer.init();
  }

  @BeforeEach
  void init() {
    dup = spy(new InMemoryDuplicateProvider(Duration.ofMinutes(2), 1000));
    limiter = mock(RateLimiter.class);
    captcha = mock(CaptchaProvider.class);
    bot = mock(BotService.class, RETURNS_DEEP_STUBS); // deep-stub sender()

    isp = new AntiSpamInterceptor(dup, limiter, captcha, Set.of("bad.com"));

    var localizer = new MessageLocalizerImpl("i18n/messages", Locale.forLanguageTag("ru"));
    when(bot.localizer()).thenReturn(localizer);
  }

  /* ─────────────────────────────────────────────────────────────── */

  @Test
  @DisplayName("duplicate ⇒ CAPTCHA + DropUpdateException")
  void duplicateTriggersCaptcha() {
    when(limiter.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true); // flood OK

    Update u1 = upd(100, 10, "Hello");
    Update u2 = upd(100, 10, "Hello"); // дубликат
    BotRequest<?> req = mock(BotRequest.class, RETURNS_DEEP_STUBS);
    when(req.service()).thenReturn(bot);

    isp.preHandle(u1, req); // первый проходит

    assertThatThrownBy(() -> isp.preHandle(u2, req))
        .isInstanceOf(DropUpdateException.class)
        .hasMessageContaining("duplicate");

    verify(captcha).question(any());
    /*  бот может спросить sender(), поэтому проверяем именно execute-вызовы */
    verify(bot.sender(), never()).execute(isA(DeleteMessage.class));
  }

  @Test
  @DisplayName("flood-gate ⇒ CAPTCHA")
  void floodTriggersCaptcha() {
    when(limiter.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(false); // сразу limit

    Update u = upd(200, 77, "spam");
    BotRequest<?> req = mock(BotRequest.class, RETURNS_DEEP_STUBS);
    when(req.service()).thenReturn(bot);

    assertThatThrownBy(() -> isp.preHandle(u, req))
        .isInstanceOf(DropUpdateException.class)
        .hasMessageContaining("flood");

    verify(captcha).question(any());
    verify(bot.sender(), never()).execute(isA(DeleteMessage.class));
  }

  @Test
  @DisplayName("malicious link ⇒ delete + warning")
  void maliciousLinkBlocked() {
    when(limiter.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true);

    Update u = upd(300, 42, "http://bad.com/phish");
    BotRequest<?> req = mock(BotRequest.class, RETURNS_DEEP_STUBS);

    when(req.service()).thenReturn(bot);
    when(req.msgKey(anyString())).thenCallRealMethod();

    assertThatThrownBy(() -> isp.preHandle(u, req))
        .isInstanceOf(DropUpdateException.class)
        .hasMessageContaining("url");

    /* порядок: delete(), затем warning-message */
    InOrder io = inOrder(bot.sender());
    io.verify(bot.sender()).execute(isA(DeleteMessage.class));
    io.verify(bot.sender())
        .execute(
            (SendMessage)
                argThat(
                    m ->
                        m instanceof SendMessage
                            && ((SendMessage) m).getText().contains("заблокирована")));
    io.verifyNoMoreInteractions();

    verifyNoInteractions(captcha); // без капчи
  }

  @Test
  @DisplayName("good message ➜ no side-effects")
  void okMessagePasses() {
    when(limiter.tryAcquire(anyString(), anyInt(), anyInt())).thenReturn(true);

    Update u = upd(400, 99, "Just hello");
    BotRequest<?> req = mock(BotRequest.class, RETURNS_DEEP_STUBS);
    when(req.service()).thenReturn(bot);

    isp.preHandle(u, req); // no exception

    verifyNoInteractions(captcha);
    verify(bot.sender(), never()).execute(any(SendMessage.class));
    verify(bot.sender(), never()).execute(any(DeleteMessage.class));
  }

  /* ===== helpers ===================================================== */

  /** minimal Update with Message */
  private static Update upd(long chatId, long userId, String txt) {
    Message m = new Message();
    Chat chat = new Chat();
    chat.setId(chatId);
    m.setChat(chat);
    m.setMessageId(123);
    m.setText(txt);

    User from = new User();
    from.setId(userId);
    m.setFrom(from);

    Update u = new Update();
    u.setMessage(m);

    return u;
  }
}
