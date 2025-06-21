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

package io.github.tgkit.core.validator;

import io.github.tgkit.core.exception.ValidationException;
import io.github.tgkit.core.i18n.MessageKey;
import java.util.Objects;
import java.util.function.Predicate;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Универсальный валидатор для любых типов {@code T}.
 *
 * @param <T> тип проверяемого значения
 */
@FunctionalInterface
public interface Validator<T> {

  /**
   * Создаёт {@code Validator<T>} из произвольного {@link Predicate} и ключа ошибки.
   *
   * @param predicate условие прохождения
   * @param errorKey  ключ для локализованного сообщения об ошибке
   */
  static <T> @NonNull Validator<T> of(
      @NonNull Predicate<T> predicate, @NonNull MessageKey errorKey) {
    return v -> {
      if (!predicate.test(v)) {
        throw ValidationException.of(errorKey);
      }
    };
  }

  /**
   * Проверяет значение на корректность.
   *
   * @param value входное значение
   * @throws ValidationException если проверка не прошла
   */
  void validate(@Nullable T value) throws ValidationException;

  /**
   * Комбинирует два валидатора: сначала этот, потом другой.
   */
  default @NonNull Validator<T> and(@NonNull Validator<? super T> other) {
    Objects.requireNonNull(other, "other");
    return v -> {
      validate(v);
      other.validate(v);
    };
  }

  /**
   * «Или»: если первый не прошёл, пытаем второй.
   */
  default @NonNull Validator<T> or(@NonNull Validator<? super T> other) {
    Objects.requireNonNull(other, "other");
    return v -> {
      try {
        validate(v);
      } catch (ValidationException ex) {
        other.validate(v);
      }
    };
  }
}
