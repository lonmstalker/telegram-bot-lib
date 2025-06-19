package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Описатель одного шага wizard’а.
 *
 * @param <M> тип DTO-модели
 * @param <I> исходный тип ввода (до парсинга)
 * @param <O> тип после парсинга/валидации
 */
@Builder
@Getter
@Setter
public class StepDefinition<M, I, O> {

    /** Уникальный идентификатор шага. */
    final @NonNull String id;

    /** Список валидаторов выходного значения. */
    @Builder.Default
    final List<Validator<O>> validators = new ArrayList<>();

    /** Один (или несколько) MessageKey’ей: A/B-варианты вопроса. */
    @Builder.Default
    final List<MessageKey> questionKeys = new ArrayList<>();

    /** Парсер из BotRequest→I. */
    Function<BotRequest<?>, I> parser;

    /** Проверка типа запроса до парсинга. */
    Validator<BotRequest<?>> typeValidator;

    /** Сеттер: сохраняет O в модель M. */
    BiConsumer<M, O> setter;

    /** Разрешено ли вернуться назад. */
    @Builder.Default
    boolean canBack = true;

    /** Хук при нажатии «назад». */
    BiConsumer<BotRequest<?>, M> onBack;

    /** Разрешено ли пропустить шаг. */
    @Builder.Default
    boolean canSkip = false;

    /** Хук при пропуске. */
    BiConsumer<BotRequest<?>, M> onSkip;

    /** Разрешено ли отменить сессию. */
    @Builder.Default
    boolean canCancel = true;

    /** Хук при отмене. */
    BiConsumer<BotRequest<?>, M> onCancel;

    /** Бизнес-логика ветвления: по model+ответу выдаёт следующий stepId. */
    @Builder.Default
    Function<M, String> nextSupplier = m -> null;

    /** Таймаут до напоминания. */
    Duration timeout;

    /** Сообщение-напоминание, если таймаут исчерпан. */
    MessageKey reminderKey;

    /** Pre–finish проверка перед завершением сценария. */
    Predicate<M> preFinishChecker;

    /** Куда перейти, если pre–finishChecker вернул false. */
    String preFinishFailStepId;
}
