package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.validator.Validator;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import lombok.NoArgsConstructor;
import lombok.AccessLevel;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;

import java.util.List;

/**
 * Валидаторы для опросов (Poll из Telegram API).
 * <p>
 * Проверяют количество вариантов и длину текста каждого варианта.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PollValidators {

    private static final int MIN_OPTS = 2, MAX_OPTS = 10, MAX_TEXT = 100;

    /**
     * Проверяет, что количество вариантов находится в диапазоне [2..10].
     *
     * @return Validator<Poll> с ключом "error.poll.count"
     */
    public static Validator<@NonNull Poll> optionsCount() {
        return Validator.of(
                p -> {
                    List<?> opts = p.getOptions();
                    return opts != null && opts.size() >= MIN_OPTS && opts.size() <= MAX_OPTS;
                },
                MessageKey.of("error.poll.count", MIN_OPTS, MAX_OPTS)
        );
    }

    /**
     * Проверяет, что каждый вариант не длиннее 100 символов.
     *
     * @return Validator<Poll> с ключом "error.poll.optionLength"
     */
    public static Validator<@NonNull Poll> optionTextLength() {
        return Validator.of(
                p -> p.getOptions().stream().allMatch(o -> o.getText().length() <= MAX_TEXT),
                MessageKey.of("error.poll.optionLength", MAX_TEXT)
        );
    }
}
