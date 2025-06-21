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
package io.github.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.payments.Invoice;

/**
 * Валидаторы для платежей (Invoice из Telegram API).
 *
 * <p>Проверяют корректность суммы и формат валюты.
 */
public final class PaymentValidators {

  private PaymentValidators() {}

  private static final long MAX_CENTS = 1_000_000L; // $10 000

  /**
   * Проверяет, что сумма >0 и ≤$10 000.
   *
   * @return Validator<Invoice> с ключом "error.payment.amount"
   */
  public static Validator<Invoice> validAmount() {
    return validAmount(0, MAX_CENTS);
  }

  /**
   * Проверяет, что сумма >{@param minCents} и ≤{@param maxCents}.
   *
   * @return Validator<Invoice> с ключом "error.payment.amount"
   */
  public static Validator<@NonNull Invoice> validAmount(long minCents, long maxCents) {
    return Validator.of(
        inv -> inv.getTotalAmount() > minCents && inv.getTotalAmount() <= maxCents,
        MessageKey.of("error.payment.amount", minCents, MAX_CENTS));
  }

  /**
   * Проверяет, что код валюты соответствует ISO-4217 (3 заглавных буквы).
   *
   * @return Validator<Invoice> с ключом "error.payment.currency"
   */
  public static Validator<@NonNull Invoice> validCurrency() {
    return Validator.of(
        inv -> inv.getCurrency() != null && inv.getCurrency().matches("^[A-Z]{3}$"),
        MessageKey.of("error.payment.currency"));
  }
}
