package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.i18n.MessageKey;
import io.lonmstalker.tgkit.core.validator.Validator;
import java.util.regex.Pattern;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Contact;

/**
 * Валидаторы для контактов (Contact из Telegram API).
 *
 * <p>Проверяют формат телефона по E.164 и длину имени.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ContactValidators {

  private static final Pattern E164 = Pattern.compile("^\\+\\d{1,15}$");
  private static final int MAX_NAME = 255;

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
