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

import io.github.tgkit.core.i18n.MessageKey;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Message;

/** Валидаторы содержимого {@link Message Telegram-сообщения}. */
public final class MessageValidators {
  private MessageValidators() {}

  public static @NonNull Validator<Message> hasText() {
    return Validator.of(
        msg -> msg.getText() != null && !msg.getText().isBlank(), MessageKey.of("error.noText"));
  }

  public static @NonNull Validator<Message> hasPhoto() {
    return Validator.of(
        msg -> msg.getPhoto() != null && !msg.getPhoto().isEmpty(), MessageKey.of("error.noPhoto"));
  }

  public static @NonNull Validator<Message> hasVideo() {
    return Validator.of(msg -> msg.getVideo() != null, MessageKey.of("error.noVideo"));
  }

  public static @NonNull Validator<Message> hasDocument() {
    return Validator.of(msg -> msg.getDocument() != null, MessageKey.of("error.noDocument"));
  }

  public static @NonNull Validator<Message> hasAudio() {
    return Validator.of(msg -> msg.getAudio() != null, MessageKey.of("error.noAudio"));
  }

  public static @NonNull Validator<Message> hasVoice() {
    return Validator.of(msg -> msg.getVoice() != null, MessageKey.of("error.noVoice"));
  }

  public static @NonNull Validator<Message> hasSticker() {
    return Validator.of(Message::hasSticker, MessageKey.of("error.noSticker"));
  }

  public static @NonNull Validator<Message> hasContact() {
    return Validator.of(msg -> msg.getContact() != null, MessageKey.of("error.noContact"));
  }

  public static @NonNull Validator<Message> hasLocation() {
    return Validator.of(msg -> msg.getLocation() != null, MessageKey.of("error.noLocation"));
  }

  public static @NonNull Validator<Message> hasVenue() {
    return Validator.of(msg -> msg.getVenue() != null, MessageKey.of("error.noVenue"));
  }

  public static @NonNull Validator<Message> hasAnimation() {
    return Validator.of(msg -> msg.getAnimation() != null, MessageKey.of("error.noAnimation"));
  }

  public static @NonNull Validator<Message> hasGame() {
    return Validator.of(msg -> msg.getGame() != null, MessageKey.of("error.noGame"));
  }

  public static @NonNull Validator<Message> hasInvoice() {
    return Validator.of(msg -> msg.getInvoice() != null, MessageKey.of("error.noInvoice"));
  }

  public static @NonNull Validator<Message> hasSuccessfulPayment() {
    return Validator.of(
        msg -> msg.getSuccessfulPayment() != null, MessageKey.of("error.noSuccessfulPayment"));
  }

  public static @NonNull Validator<Message> hasPoll() {
    return Validator.of(msg -> msg.getPoll() != null, MessageKey.of("error.noPoll"));
  }

  public static @NonNull Validator<Message> hasDice() {
    return Validator.of(msg -> msg.getDice() != null, MessageKey.of("error.noDice"));
  }

  public static @NonNull Validator<Message> hasWebAppData() {
    return Validator.of(msg -> msg.getWebAppData() != null, MessageKey.of("error.noWebAppData"));
  }

  public static @NonNull Validator<Message> hasPassportData() {
    return Validator.of(
        msg -> msg.getPassportData() != null, MessageKey.of("error.noPassportData"));
  }

  public static @NonNull Validator<Message> hasReplyMarkup() {
    return Validator.of(msg -> msg.getReplyMarkup() != null, MessageKey.of("error.noReplyMarkup"));
  }

  public static @NonNull Validator<Message> hasPinnedMessage() {
    return Validator.of(
        msg -> msg.getPinnedMessage() != null, MessageKey.of("error.noPinnedMessage"));
  }

  public static @NonNull Validator<Message> hasNewChatMembers() {
    return Validator.of(
        msg -> msg.getNewChatMembers() != null && !msg.getNewChatMembers().isEmpty(),
        MessageKey.of("error.noNewChatMembers"));
  }

  public static @NonNull Validator<Message> hasLeftChatMember() {
    return Validator.of(
        msg -> msg.getLeftChatMember() != null, MessageKey.of("error.noLeftChatMember"));
  }

  public static @NonNull Validator<Message> hasNewChatTitle() {
    return Validator.of(
        msg -> msg.getNewChatTitle() != null, MessageKey.of("error.noNewChatTitle"));
  }

  public static @NonNull Validator<Message> hasNewChatPhoto() {
    return Validator.of(
        msg -> msg.getNewChatPhoto() != null && !msg.getNewChatPhoto().isEmpty(),
        MessageKey.of("error.noNewChatPhoto"));
  }

  public static @NonNull Validator<Message> isGroupCreated() {
    return Validator.of(Message::getGroupchatCreated, MessageKey.of("error.notGroupCreated"));
  }

  public static @NonNull Validator<Message> isSuperGroupCreated() {
    return Validator.of(Message::getSuperGroupCreated, MessageKey.of("error.notSuperGroupCreated"));
  }

  public static @NonNull Validator<Message> isChannelCreated() {
    return Validator.of(Message::getChannelChatCreated, MessageKey.of("error.notChannelCreated"));
  }

  public static @NonNull Validator<Message> hasMigrateToChat() {
    return Validator.of(
        msg -> msg.getMigrateToChatId() != null, MessageKey.of("error.noMigrateToChat"));
  }

  public static @NonNull Validator<Message> hasMigrateFromChat() {
    return Validator.of(
        msg -> msg.getMigrateFromChatId() != null, MessageKey.of("error.noMigrateFromChat"));
  }

  public static @NonNull Validator<Message> hasForwardFrom() {
    return Validator.of(msg -> msg.getForwardFrom() != null, MessageKey.of("error.noForwardFrom"));
  }

  public static @NonNull Validator<Message> hasForwardDate() {
    return Validator.of(msg -> msg.getForwardDate() != null, MessageKey.of("error.noForwardDate"));
  }

  public static @NonNull Validator<Message> hasReplyToMessage() {
    return Validator.of(
        msg -> msg.getReplyToMessage() != null, MessageKey.of("error.noReplyToMessage"));
  }
}
