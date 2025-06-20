package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.payments.Invoice;

/**
 * Валидаторы для платежей (Invoice из Telegram API).
 *
 * <p>Проверяют корректность суммы и формат валюты.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class PaymentValidators {

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
