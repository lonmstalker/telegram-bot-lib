package io.lonmstalker.tgkit.security.captcha;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizerImpl;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.store.UserKVStore;
import io.lonmstalker.tgkit.security.TestUtils;
import io.lonmstalker.tgkit.security.captcha.provider.MathCaptchaProvider;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import java.time.Duration;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;
import org.apache.commons.lang3.Range;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/*======================================================================
 *  Проверяем:
 *   1) question()  ― корректно формирует текст, варианты и кэширует ответ
 *   2) verify()    ―   true    → удаляет капчу-сообщение, чистит cache
 *                    false   → ничего не удаляет
 *  Зависимости BotDSL/Request/Sender мокаются (deep-stubs).
 *--------------------------------------------------------------------*/
class MathCaptchaProviderServiceTest {

  MathCaptchaProvider captcha;
  BotRequest<?> req;
  BotUserInfo user;
  UserKVStore kv;
  MathCaptchaProviderStore cacheSpy;

  static {
    BotCoreInitializer.init();
    BotSecurityInitializer.init();
  }

  @BeforeEach
  void setUp() {
    captcha =
        MathCaptchaProvider.builder()
            .ttl(Duration.ofMinutes(10))
            .numberRange(Range.of(1, 9))
            .wrongCount(2)
            .allowedOps(MathCaptchaOperations.OPERATIONS)
            .build();

    // Caffeine cache из билдера – подменяем spy, чтобы проверять state
    cacheSpy = spy((MathCaptchaProviderStore) TestUtils.extract(captcha, "answersStore"));
    TestUtils.setField(captcha, "answersStore", cacheSpy);

    /* ------- mock BotRequest и окружение ------- */
    user = mock(BotUserInfo.class);
    when(user.chatId()).thenReturn(100L);
    when(user.userId()).thenReturn(200L);
    when(user.roles()).thenReturn(Set.of());

    kv = mock(UserKVStore.class);

    // deep-stub: msgKey() → builder → build() возвращает SendMessage
    req = mock(BotRequest.class, RETURNS_DEEP_STUBS);
    BotService service = mock(BotService.class);
    when(req.user()).thenReturn(user);
    when(req.service()).thenReturn(service);
    when(req.service().userKVStore()).thenReturn(kv);
    doNothing().when(req).requiredChatId();

    MessageLocalizer loc = new MessageLocalizerImpl("i18n/messages", Locale.US);
    when(req.service().localizer()).thenReturn(loc);

    // builder.build() отдаёт заранее сконструированный SendMessage
    when(req.msgKey(anyString(), any(), any())).thenCallRealMethod(); // позволит захватить key
  }

  @Test
  void questionGeneratesCorrectVariantsAndCachesAnswer() {
    SendMessage sm = captcha.question(req);

    // текст содержит символ операции
    assertThat(sm.getText()).matches(".*[+\\-*/].*");

    // варианты = answer + wrongCount
    InlineKeyboardMarkup kb = (InlineKeyboardMarkup) sm.getReplyMarkup();
    int buttons = kb.getKeyboard().size(); // col() — каждая кнопка в строке
    assertThat(buttons).isEqualTo(3); // 1 правильный + 2 неверных

    // ответ записан в cache
    verify(cacheSpy).put(eq(100L), anyInt(), any());
  }

  @Test
  void verifyTrueDeletesMsgAndClearsState() {
    // кладём правильный ответ в cache
    cacheSpy.put(100L, 42, Duration.ofMinutes(10));

    // при успешной верификации вызывается delete()
    AtomicBoolean deleted = new AtomicBoolean(false);
    when(req.delete(anyLong()).send())
        .thenAnswer(
            i -> {
              deleted.set(true);
              return null;
            });

    // emulate kv store returning stored msgId
    when(kv.get(200L, "captcha_msg_id")).thenReturn("555");

    boolean ok = captcha.verify(req, "42");
    assertThat(ok).isTrue();
    assertThat(deleted).isTrue();

    // cache должен очиститься
    assertThat(cacheSpy.pop(100L)).isNull();
  }

  @Test
  void verifyFalseReturnsFalseWithoutSideEffects() {
    cacheSpy.put(100L, 10, Duration.ofMinutes(10));

    boolean ok = captcha.verify(req, "999");
    assertThat(ok).isFalse();

    // cache уже удалили? (должны)
    assertThat(cacheSpy.pop(100L)).isNull();
    verify(req, never()).delete(anyLong());
    verify(kv, never()).get(anyLong(), anyString());
  }
}
