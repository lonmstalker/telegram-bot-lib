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
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Описатель одного шага wizard’а.
 *
 * @param <M> тип DTO-модели
 * @param <I> исходный тип ввода (до парсинга)
 * @param <O> тип после парсинга/валидации
 */
public class StepDefinition<M, I, O> {

  /** Уникальный идентификатор шага. */
  final @NonNull String id;

  /** Список валидаторов выходного значения. */
  final List<Validator<O>> validators = new ArrayList<>();

  /** Один (или несколько) MessageKey’ей: A/B-варианты вопроса. */
  final List<MessageKey> questionKeys = new ArrayList<>();

  /** Парсер из BotRequest→I. */
  Function<BotRequest<?>, I> parser;

  /** Проверка типа запроса до парсинга. */
  Validator<BotRequest<?>> typeValidator;

  /** Сеттер: сохраняет O в модель M. */
  BiConsumer<M, O> setter;

  /** Разрешено ли вернуться назад. */
  boolean canBack = true;

  /** Хук при нажатии «назад». */
  BiConsumer<BotRequest<?>, M> onBack;

  /** Разрешено ли пропустить шаг. */
  boolean canSkip = false;

  /** Хук при пропуске. */
  BiConsumer<BotRequest<?>, M> onSkip;

  /** Разрешено ли отменить сессию. */
  boolean canCancel = true;

  /** Хук при отмене. */
  BiConsumer<BotRequest<?>, M> onCancel;

  /** Бизнес-логика ветвления: по model+ответу выдаёт следующий stepId. */
  Function<M, String> nextSupplier = m -> null;

  /** Таймаут до напоминания. */
  Duration timeout;

  /** Сообщение-напоминание, если таймаут исчерпан. */
  MessageKey reminderKey;

  /** Pre–finish проверка перед завершением сценария. */
  Predicate<M> preFinishChecker;

  /** Куда перейти, если pre–finishChecker вернул false. */
  String preFinishFailStepId;

  private StepDefinition(Builder<M, I, O> b) {
    this.id = Objects.requireNonNull(b.id, "id");
    this.validators.addAll(b.validators);
    this.questionKeys.addAll(b.questionKeys);
    this.parser = b.parser;
    this.typeValidator = b.typeValidator;
    this.setter = b.setter;
    this.canBack = b.canBack;
    this.onBack = b.onBack;
    this.canSkip = b.canSkip;
    this.onSkip = b.onSkip;
    this.canCancel = b.canCancel;
    this.onCancel = b.onCancel;
    this.nextSupplier = b.nextSupplier;
    this.timeout = b.timeout;
    this.reminderKey = b.reminderKey;
    this.preFinishChecker = b.preFinishChecker;
    this.preFinishFailStepId = b.preFinishFailStepId;
  }

  /** Возвращает новый builder. */
  public static <M, I, O> Builder<M, I, O> builder() {
    return new Builder<>();
  }

  // ----- getters -----
  public @NonNull String getId() {
    return id;
  }

  public @NonNull List<Validator<O>> getValidators() {
    return validators;
  }

  public @NonNull List<MessageKey> getQuestionKeys() {
    return questionKeys;
  }

  public @Nullable Function<BotRequest<?>, I> getParser() {
    return parser;
  }

  public void setParser(Function<BotRequest<?>, I> parser) {
    this.parser = parser;
  }

  public @Nullable Validator<BotRequest<?>> getTypeValidator() {
    return typeValidator;
  }

  public void setTypeValidator(Validator<BotRequest<?>> typeValidator) {
    this.typeValidator = typeValidator;
  }

  public @Nullable BiConsumer<M, O> getSetter() {
    return setter;
  }

  public void setSetter(BiConsumer<M, O> setter) {
    this.setter = setter;
  }

  public boolean isCanBack() {
    return canBack;
  }

  public void setCanBack(boolean canBack) {
    this.canBack = canBack;
  }

  public @Nullable BiConsumer<BotRequest<?>, M> getOnBack() {
    return onBack;
  }

  public void setOnBack(BiConsumer<BotRequest<?>, M> onBack) {
    this.onBack = onBack;
  }

  public boolean isCanSkip() {
    return canSkip;
  }

  public void setCanSkip(boolean canSkip) {
    this.canSkip = canSkip;
  }

  public @Nullable BiConsumer<BotRequest<?>, M> getOnSkip() {
    return onSkip;
  }

  public void setOnSkip(BiConsumer<BotRequest<?>, M> onSkip) {
    this.onSkip = onSkip;
  }

  public boolean isCanCancel() {
    return canCancel;
  }

  public void setCanCancel(boolean canCancel) {
    this.canCancel = canCancel;
  }

  public @Nullable BiConsumer<BotRequest<?>, M> getOnCancel() {
    return onCancel;
  }

  public void setOnCancel(BiConsumer<BotRequest<?>, M> onCancel) {
    this.onCancel = onCancel;
  }

  public @NonNull Function<M, String> getNextSupplier() {
    return nextSupplier;
  }

  public void setNextSupplier(Function<M, String> nextSupplier) {
    this.nextSupplier = nextSupplier;
  }

  public @Nullable Duration getTimeout() {
    return timeout;
  }

  public void setTimeout(Duration timeout) {
    this.timeout = timeout;
  }

  public @Nullable MessageKey getReminderKey() {
    return reminderKey;
  }

  public void setReminderKey(MessageKey reminderKey) {
    this.reminderKey = reminderKey;
  }

  public @Nullable Predicate<M> getPreFinishChecker() {
    return preFinishChecker;
  }

  public void setPreFinishChecker(Predicate<M> preFinishChecker) {
    this.preFinishChecker = preFinishChecker;
  }

  public @Nullable String getPreFinishFailStepId() {
    return preFinishFailStepId;
  }

  public void setPreFinishFailStepId(String preFinishFailStepId) {
    this.preFinishFailStepId = preFinishFailStepId;
  }

  /** Builder для {@link StepDefinition}. */
  public static final class Builder<M, I, O> {
    private String id;
    private List<Validator<O>> validators = new ArrayList<>();
    private List<MessageKey> questionKeys = new ArrayList<>();
    private Function<BotRequest<?>, I> parser;
    private Validator<BotRequest<?>> typeValidator;
    private BiConsumer<M, O> setter;
    private boolean canBack = true;
    private BiConsumer<BotRequest<?>, M> onBack;
    private boolean canSkip = false;
    private BiConsumer<BotRequest<?>, M> onSkip;
    private boolean canCancel = true;
    private BiConsumer<BotRequest<?>, M> onCancel;
    private Function<M, String> nextSupplier = m -> null;
    private Duration timeout;
    private MessageKey reminderKey;
    private Predicate<M> preFinishChecker;
    private String preFinishFailStepId;

    /** Устанавливает идентификатор шага. */
    public Builder<M, I, O> id(@NonNull String id) {
      this.id = id;
      return this;
    }

    /** Собирает объект {@link StepDefinition}. */
    public StepDefinition<M, I, O> build() {
      return new StepDefinition<>(this);
    }
  }
}
