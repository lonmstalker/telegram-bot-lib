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
package io.lonmstalker.tgkit.core.bot;

import io.lonmstalker.tgkit.core.BotRequestConverter;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Реестр конвертеров для каждого BotRequestType вместо большого switch. */
class BotRequestConverterImpl implements BotRequestConverter<BotApiObject> {

  private final Map<BotRequestType, Function<Update, BotApiObject>> registry =
      new EnumMap<>(BotRequestType.class);

  BotRequestConverterImpl() {
    registry.put(BotRequestType.MESSAGE, Update::getMessage);
    registry.put(BotRequestType.EDITED_MESSAGE, Update::getEditedMessage);
    registry.put(BotRequestType.CHANNEL_POST, Update::getChannelPost);
    registry.put(BotRequestType.EDITED_CHANNEL_POST, Update::getEditedChannelPost);
    registry.put(BotRequestType.CALLBACK_QUERY, Update::getCallbackQuery);
    registry.put(BotRequestType.INLINE_QUERY, Update::getInlineQuery);
    registry.put(BotRequestType.CHOSEN_INLINE_QUERY, Update::getChosenInlineQuery);
    registry.put(BotRequestType.SHIPPING_QUERY, Update::getShippingQuery);
    registry.put(BotRequestType.PRE_CHECKOUT_QUERY, Update::getPreCheckoutQuery);
    registry.put(BotRequestType.POLL, Update::getPoll);
    registry.put(BotRequestType.POLL_ANSWER, Update::getPollAnswer);
    registry.put(BotRequestType.CHAT_MEMBER, Update::getChatMember);
    registry.put(BotRequestType.MY_CHAT_MEMBER, Update::getMyChatMember);
    registry.put(BotRequestType.CHAT_JOIN_REQUEST, Update::getChatJoinRequest);
    registry.put(BotRequestType.MESSAGE_REACTION, Update::getMessageReaction);
    registry.put(BotRequestType.MESSAGE_REACTION_COUNT, Update::getMessageReactionCount);
    registry.put(BotRequestType.CHAT_BOOST, Update::getChatBoost);
    registry.put(BotRequestType.REMOVED_CHAT_BOOST, Update::getRemovedChatBoost);
  }

  @Override
  public @NonNull BotApiObject convert(@NonNull Update update, @NonNull BotRequestType type) {
    Function<Update, BotApiObject> fn = registry.get(type);
    if (fn == null) {
      throw new BotApiException("Unsupported request type: " + type);
    }
    BotApiObject obj = fn.apply(update);
    type.checkType(obj.getClass());
    return obj;
  }
}
