package io.lonmstalker.tgkit.security.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.security.CaptchaProvider;
import lombok.Builder;
import lombok.NonNull;
import org.apache.commons.lang3.Range;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.security.SecureRandom;
import java.text.MessageFormat;
import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Математическая CAPTCHA с несколькими операциями.
 */
public final class MathCaptcha implements CaptchaProvider {
    private final SecureRandom rnd;
    private final Range<Integer> numberRange;
    private final int wrongCount;
    private final List<MathCaptchaOperations> allowedOps;
    private final Cache<Long, Integer> answers;

    @Builder
    public MathCaptcha(
            @NonNull Duration ttl,
            @NonNull Range<Integer> numberRange,
            int wrongCount,
            @NonNull List<MathCaptchaOperations> allowedOps
    ) {
        this.rnd = new SecureRandom();
        this.numberRange = numberRange;
        this.wrongCount = Math.max(wrongCount, 1);
        this.allowedOps = List.copyOf(allowedOps);
        this.answers = Caffeine.newBuilder()
                .expireAfterWrite(ttl)
                .maximumSize(10_000)
                .build();
    }

    // ---------- CaptchaProvider ----------
    @Override
    public SendMessage question(long chatId, @NonNull MessageLocalizer localizer) {
        int a = randomInt();
        int b = randomInt();
        MathCaptchaOperations op = allowedOps.get(rnd.nextInt(allowedOps.size()));

        int answer = op.apply(a, b);
        answers.put(chatId, answer);

        // Локализованный текст "Сколько будет {0} + {1}?"
        String text = MessageFormat.format(
                localizer.get("captcha.math.question." + op.name()), a, b
        );

        // Кнопки: правильный + N ложных
        List<Integer> variants = generateVariants(answer);
        Collections.shuffle(variants, rnd);

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                variants.stream()
                        .map(this::button)
                        .map(List::of)
                        .collect(Collectors.toList())          // по одному в строке
        );

        SendMessage msg = new SendMessage(Long.toString(chatId), text);
        msg.setReplyMarkup(markup);
        return msg;
    }

    @Override
    public boolean verify(long chatId, @NonNull String answer) {
        Integer expected = answers.asMap().remove(chatId);     // атомарно удаляем
        return expected != null && String.valueOf(expected).equals(answer);
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
