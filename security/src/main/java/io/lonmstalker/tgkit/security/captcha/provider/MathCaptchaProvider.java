package io.lonmstalker.tgkit.security.captcha.provider;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import io.lonmstalker.tgkit.security.captcha.InMemoryMathCaptchaProviderStore;
import io.lonmstalker.tgkit.security.captcha.MathCaptchaOperations;
import io.lonmstalker.tgkit.security.captcha.MathCaptchaProviderStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.*;
import lombok.Builder;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/** Математическая CAPTCHA с несколькими операциями. */
public final class MathCaptchaProvider implements CaptchaProvider {
  private static final String CAPTCHA_KEY = "captcha_msg_id";
  private final int wrongCount;
  private final Duration ttl;
  private final SecureRandom rnd;
  private final Range<Integer> numberRange;
  private final List<MathCaptchaOperations> allowedOps;
  private final MathCaptchaProviderStore answersStore;

  @Builder
  public MathCaptchaProvider(
      @NonNull Duration ttl,
      @NonNull Range<Integer> numberRange,
      int wrongCount,
      @Nullable MathCaptchaProviderStore store,
      @NonNull List<MathCaptchaOperations> allowedOps) {
    this.ttl = ttl;
    this.rnd = new SecureRandom();
    this.numberRange = numberRange;
    this.wrongCount = Math.max(wrongCount, 1);
    this.allowedOps = List.copyOf(allowedOps);
    this.answersStore = store != null ? store : new InMemoryMathCaptchaProviderStore(ttl, 1000);
  }

  @Override
  public @NonNull SendMessage question(@NonNull BotRequest<?> request) {
    request.requiredChatId();

    int a = randomInt();
    int b = randomInt();
    MathCaptchaOperations op = allowedOps.get(rnd.nextInt(allowedOps.size()));

    int answer = op.apply(a, b);
    answersStore.put(Objects.requireNonNull(request.user().chatId()), answer, ttl);

    // Кнопки: правильный + N ложных
    List<Integer> variants = generateVariants(answer);
    Collections.shuffle(variants, rnd);

    // Локализованный текст "Сколько будет {0} + {1}?"
    return request
        .msgKey("captcha.math.question", a, b)
        .keyboard(kb -> kb.colFrom(variants, this::button))
        .disableNotif()
        .hooks(
            id ->
                request
                    .service()
                    .userKVStore()
                    .put(
                        Objects.requireNonNull(request.user().userId()),
                        CAPTCHA_KEY,
                        String.valueOf(id)),
            ex -> {})
        .build();
  }

  @Override
  public boolean verify(@NonNull BotRequest<?> request, @NonNull String answer) {
    Integer expected = answersStore.pop(Objects.requireNonNull(request.user().chatId()));
    boolean success = expected != null && String.valueOf(expected).equals(answer);

    if (success) {
      var captchaMsgId =
          request
              .service()
              .userKVStore()
              .get(Objects.requireNonNull(request.user().userId()), CAPTCHA_KEY);
      if (captchaMsgId != null) {
        request.delete(Long.parseLong(captchaMsgId)).send();
      }
    }

    return success;
  }

  private int randomInt() {
    return rnd.nextInt(numberRange.getMaximum() - numberRange.getMinimum() + 1)
        + numberRange.getMinimum();
  }

  private List<Integer> generateVariants(int answer) {
    Set<Integer> set = new HashSet<>();
    set.add(answer);
    while (set.size() < wrongCount + 1) {
      int delta = rnd.nextInt(numberRange.getMaximum()) + 1;
      set.add(answer + (rnd.nextBoolean() ? delta : -delta));
    }
    return new ArrayList<>(set);
  }

  private InlineKeyboardButton button(int value) {
    InlineKeyboardButton btn = new InlineKeyboardButton(String.valueOf(value));
    btn.setCallbackData(String.valueOf(value));
    return btn;
  }
}
