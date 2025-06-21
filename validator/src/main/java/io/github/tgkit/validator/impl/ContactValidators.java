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

import io.github.tgkit.core.i18n.MessageKey;
import io.github.tgkit.core.validator.Validator;
import java.util.regex.Pattern;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Contact;

/**
 * Валидаторы для контактов (Contact из Telegram API).
 *
 * <p>Проверяют формат телефона по E.164 и длину имени.
 */
public final class ContactValidators {

  private static final Pattern E164 = Pattern.compile("^\\+\\d{1,15}$");
  private static final int MAX_NAME = 255;
  private ContactValidators() {
  }

  /**
   * Проверяет, что телефон соответствует формату E.164.
   *
   * @return Validator<Contact> с ключом "error.contact.phone"
   */
  public static Validator<@NonNull Contact> validPhone() {
    return Validator.of(
        c -> c.getPhoneNumber() != null && E164.matcher(c.getPhoneNumber()).matches(),
        MessageKey.of("error.contact.phone"));
  }

  /**
   * Проверяет, что имя контакта не длиннее {@value MAX_NAME} символов.
   *
   * @return Validator<Contact> с ключом "error.contact.name"
   */
  public static Validator<@NonNull Contact> validName() {
    return Validator.of(
        c -> c.getFirstName() != null && c.getFirstName().length() <= MAX_NAME,
        MessageKey.of("error.contact.name", MAX_NAME));
  }
}
