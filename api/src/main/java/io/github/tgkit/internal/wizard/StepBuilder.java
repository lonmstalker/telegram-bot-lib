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
package io.github.tgkit.internal.wizard;

import io.github.tgkit.internal.BotRequest;
import io.github.tgkit.internal.i18n.MessageKey;
import io.github.tgkit.internal.validator.Validator;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.VideoNote;

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
