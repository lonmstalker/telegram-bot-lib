/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.wizard.WizardMeta;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Supplier;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Базовый абстрактный класс «wizard-сценария» (пошаговой команды).
 *
 * <p>Каждый конкретный сценарий наследует этот класс, объявляет шаги через {@link #stepId(String)}
 * и финальный обработчик через {@link #onComplete(BiConsumer)}.
 *
 * @param <M> тип DTO-модели, собираемой по шагам
 */
public abstract class Wizard<M> {

  private final String id;
  private final Supplier<M> factory;
  private BiConsumer<BotRequest<?>, M> onComplete;
  private final List<StepDefinition<M, ?, ?>> steps = new ArrayList<>();

  public String getId() {
    return id;
  }

  /**
   * Конструктор.
   *
   * @param id уникальный идентификатор сценария (из {@link WizardMeta})
   * @param modelFactory фабрика для создания пустого экземпляра {@code M}
   */
  protected Wizard(@NonNull String id, @NonNull Supplier<@NonNull M> modelFactory) {
    this.id = id;
    this.factory = modelFactory;
    this.onComplete = (botRequest, M) -> {};
  }

  /**
   * Начинает объявление нового шага.
   *
   * @param stepId уникальный идентификатор шага в рамках сценария
   * @param <I> исходный тип ввода (до конвертации)
   * @param <O> тип после конвертации и валидации
   * @return билдера шага
   */
  protected <I, O> @NonNull StepBuilder<M, I, O> stepId(@NonNull String stepId) {
    StepDefinition<M, I, O> def = StepDefinition.<M, I, O>builder().id(stepId).build();
    steps.add(def);
    return new StepBuilderImpl<>(def);
  }

  /**
   * Регистрирует callback, который будет вызван после успешного прохождения всех шагов.
   *
   * @param handler потребитель контекста и собранной модели
   */
  protected void onComplete(@NonNull BiConsumer<BotRequest<?>, M> handler) {
    this.onComplete = handler;
  }

  @NonNull Supplier<M> getFactory() {
    return factory;
  }

  @NonNull List<StepDefinition<M, ?, ?>> getSteps() {
    return Collections.unmodifiableList(steps);
  }

  @NonNull BiConsumer<BotRequest<?>, M> getOnComplete() {
    return onComplete;
  }
}
