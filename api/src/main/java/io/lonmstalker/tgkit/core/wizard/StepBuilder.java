package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.*;

/**
 * Построитель одного шага сценария: задаёт вопрос, парсит ввод, валидирует, сохраняет в модель и
 * настраивает branching/timeout/resume/A-B/preFinish.
 *
 * @param <M> модель‐DTO
 * @param <I> исходный тип ввода
 * @param <O> тип после преобразования
 */
public interface StepBuilder<M, I, O> {

  /** Варианты вопроса (A/B-тест). */
  @NonNull StepBuilder<M, I, O> ask(@NonNull MessageKey... questionKeys);

  /** Парсим простую строку. */
  @NonNull StepBuilder<M, String, String> expectText();

  /** Парсим целое. */
  @NonNull StepBuilder<M, String, Integer> expectInt();

  /** Парсим фото. */
  @NonNull StepBuilder<M, ?, List<PhotoSize>> expectPhoto();

  /** Парсим видео. */
  @NonNull StepBuilder<M, ?, Video> expectVideo();

  /** Парсим videoNote. */
  @NonNull StepBuilder<M, ?, VideoNote> expectVideoNote();

  /** Парсим локацию. */
  @NonNull StepBuilder<M, ?, Location> expectLocation();

  /** Парсим callback-кнопку по payload → строка. */
  @NonNull StepBuilder<M, ?, String> expectButtons(@NonNull List<String> payloads);

  /** Добавляет валидатор. */
  @NonNull <V extends Validator<O>> StepBuilder<M, I, O> validate(@NonNull V validator);

  /** Сохраняет результат O в модель M. */
  void save(@NonNull BiConsumer<M, O> setter);

  /** Разрешает «назад» и задаёт hook. */
  @NonNull StepBuilder<M, I, O> allowBack(@NonNull BiConsumer<BotRequest<?>, M> onBack);

  /** Разрешает «пропустить» и задаёт hook. */
  @NonNull StepBuilder<M, I, O> allowSkip(@NonNull BiConsumer<BotRequest<?>, M> onSkip);

  /** Разрешает «отменить» и задаёт hook. */
  @NonNull StepBuilder<M, I, O> allowCancel(@NonNull BiConsumer<BotRequest<?>, M> onCancel);

  /** Ветвление: условие + следующий шаг. */
  @NonNull StepBuilder<M, I, O> nextIf(@NonNull Predicate<M> condition, @NonNull String nextStepId);

  /** Таймаут и клавиша-навигация после него. */
  @NonNull StepBuilder<M, I, O> onTimeout(@NonNull Duration after, @NonNull MessageKey reminderKey);

  /** Pre–finish hook; при false идём на failStepId. */
  @NonNull StepBuilder<M, I, O> preFinish(
      @NonNull Predicate<M> checker, @NonNull String failStepId);
}
