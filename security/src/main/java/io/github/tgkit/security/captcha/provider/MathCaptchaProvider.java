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

package io.github.tgkit.security.captcha.provider;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.security.captcha.CaptchaProvider;
import io.github.tgkit.security.captcha.InMemoryMathCaptchaProviderStore;
import io.github.tgkit.security.captcha.MathCaptchaOperations;
import io.github.tgkit.security.captcha.MathCaptchaProviderStore;
import java.security.SecureRandom;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang3.Range;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Математическая CAPTCHA с несколькими операциями.
 */
public final class MathCaptchaProvider implements CaptchaProvider {
  private static final String CAPTCHA_KEY = "captcha_msg_id";
  private final int wrongCount;
  private final Duration ttl;
  private final SecureRandom rnd;
  private final Range<Integer> numberRange;
  private final List<MathCaptchaOperations> allowedOps;
  private final MathCaptchaProviderStore answersStore;

  private MathCaptchaProvider(
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

  public static Builder builder() {
    return new Builder();
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
            ex -> {
            })
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

  public static final class Builder {
    private Duration ttl;
    private Range<Integer> numberRange;
    private int wrongCount;
    private MathCaptchaProviderStore store;
    private List<MathCaptchaOperations> allowedOps;

    public Builder ttl(@NonNull Duration ttl) {
      this.ttl = ttl;
      return this;
    }

    public Builder numberRange(@NonNull Range<Integer> range) {
      this.numberRange = range;
      return this;
    }

    public Builder wrongCount(int count) {
      this.wrongCount = count;
      return this;
    }

    public Builder store(@Nullable MathCaptchaProviderStore store) {
      this.store = store;
      return this;
    }

    public Builder allowedOps(@NonNull List<MathCaptchaOperations> ops) {
      this.allowedOps = ops;
      return this;
    }

    public MathCaptchaProvider build() {
      return new MathCaptchaProvider(ttl, numberRange, wrongCount, store, allowedOps);
    }
  }
}
