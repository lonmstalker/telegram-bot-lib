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

import io.github.tgkit.validator.language.LanguageDetectionService;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.i18n.MessageKey;
import io.github.tgkit.core.validator.Validator;
import java.net.URI;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Валидации, выходящие за рамки базовых: спам-фильтры, дата/время, ссылки, валюты и т.п.
 */
public final class AdvancedValidators {

  private static final Pattern URL_PATTERN =
      Pattern.compile("(https?://[^\\s]+)", Pattern.CASE_INSENSITIVE);
  private static final Pattern CURRENCY_PATTERN =
      Pattern.compile("^\\s*([0-9]+(?:\\.[0-9]{1,2})?)\\s+([A-Z]{3})\\s*$");
  private static final List<DateTimeFormatter> FMTS =
      List.of(
          DateTimeFormatter.ISO_LOCAL_DATE,
          DateTimeFormatter.ofPattern("yyyy.MM.dd"),
          DateTimeFormatter.ofPattern("yyyy MM dd"));

  private AdvancedValidators() {
  }

  /**
   * Проверяет, что в тексте нет ссылок на домены, не входящие в список разрешённых.
   *
   * @param allowedDomains множество допустимых доменных имён (без "www.")
   * @return Validator<String>
   */
  public static Validator<@NonNull String> noForeignLinks(@NonNull Set<String> allowedDomains) {
    return Validator.of(
        text -> {
          Matcher m = URL_PATTERN.matcher(text);
          while (m.find()) {
            try {
              String host = URI.create(m.group(1)).getHost();
              if (host == null) {
                return false;
              }
              String normalized = host.toLowerCase(Locale.ROOT).replaceFirst("^www\\.", "");
              if (!allowedDomains.contains(normalized)) {
                return false;
              }
            } catch (Exception e) {
              return false;
            }
          }
          return true;
        },
        MessageKey.of("error.link.forbidden"));
  }

  /**
   * Проверяет, что вся строка соответствует формату "<amount> <CURRENCY>" где CURRENCY ∈
   * allowedCurrencies и amount > 0.
   *
   * @param allowedCurrencies множество трёхбуквенных кодов валют ISO-4217
   * @return Validator<String>
   */
  public static Validator<@NonNull String> currencyPair(@NonNull Set<String> allowedCurrencies) {
    return Validator.of(
        text -> {
          Matcher m = CURRENCY_PATTERN.matcher(text);
          if (!m.matches()) {
            return false;
          }
          double amount = Double.parseDouble(m.group(1));
          String cur = m.group(2);
          return amount > 0 && allowedCurrencies.contains(cur);
        },
        MessageKey.of("error.currency.invalid"));
  }

  /**
   * Проверяет, что текст — это ISO-дата (YYYY-MM-DD) или DD.MM.YYYY, и дата находится между сегодня
   * (включительно) и завтра+maxDaysAhead.
   *
   * @param maxDaysAhead максимально допустимое число дней в будущем
   * @return Validator<String>
   */
  public static Validator<@NonNull String> futureDate(int maxDaysAhead) {
    return Validator.of(
        text -> {
          LocalDate d = null;
          for (var fmt : FMTS) {
            try {
              d = LocalDate.parse(text.trim(), fmt);
              break;
            } catch (DateTimeParseException ignored) {
            }
          }
          if (d == null) {
            return false;
          }
          LocalDate today = LocalDate.now(ZoneOffset.UTC);
          LocalDate max = today.plusDays(maxDaysAhead);
          return !d.isBefore(today) && !d.isAfter(max);
        },
        MessageKey.of("error.date.invalidOrOutOfRange", maxDaysAhead));
  }

  /**
   * Проверяет, что определённый внешний сервисом язык текста входит в множество разрешённых.
   *
   * @param allowedLangs набор ISO-кодов языков, например "en","ru"
   * @return Validator<String>
   */
  public static Validator<@NonNull String> languageWhitelist(@NonNull Set<String> allowedLangs) {
    LanguageDetectionService l = LanguageDetectionService.get();
    return Validator.of(
        text -> {
          String lang = l.detect(text);
          return allowedLangs.contains(lang);
        },
        MessageKey.of("error.language.notAllowed"));
  }

  /**
   * Проверяет, что пользователь, инициировавший запрос (BotRequest.user.id), совпадает с полем from
   * в Telegram Message.
   *
   * @return Validator<BotRequest < ?>>
   */
  public static Validator<@NonNull BotRequest<?>> payerMatchesAuthor() {
    return Validator.of(
        req -> Objects.equals(req.user().userId(), ((Message) req.data()).getFrom().getId()),
        MessageKey.of("error.payer.mismatch"));
  }

  /**
   * Проверяет, что успешный платёж (SuccessfulPayment) пришёл не старше maxMinutes минут.
   *
   * @param maxMinutes допустимый возраст платежа в минутах
   * @return Validator<BotRequest < ?>>
   */
  public static Validator<@NonNull BotRequest<?>> invoiceFresh(int maxMinutes) {
    return Validator.of(
        req -> {
          int msgSec = ((Message) req.data()).getDate();
          long ageMin = (Instant.now().getEpochSecond() - msgSec) / 60;
          return ageMin <= maxMinutes;
        },
        MessageKey.of("error.invoice.stale", maxMinutes));
  }

  /**
   * Проверяет, что timestamp Telegram Message (getDate) не отличается от текущего времени более чем
   * на maxSeconds секунд.
   *
   * @param maxSeconds максимально допустимый дрейф в секундах
   * @return Validator<Message>
   */
  public static Validator<@NonNull Message> timestampDrift(int maxSeconds) {
    return Validator.of(
        msg -> {
          long msgSec = msg.getDate();
          long now = Instant.now().getEpochSecond();
          return Math.abs(now - msgSec) <= maxSeconds;
        },
        MessageKey.of("error.message.clockDrift", maxSeconds));
  }
}
