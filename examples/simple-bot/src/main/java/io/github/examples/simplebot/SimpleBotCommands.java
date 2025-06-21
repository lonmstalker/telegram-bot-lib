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

package io.github.examples.simplebot;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.annotation.BotHandler;
import io.github.tgkit.core.annotation.matching.AlwaysMatch;
import io.github.tgkit.core.annotation.matching.MessageContainsMatch;
import io.github.tgkit.core.annotation.matching.MessageRegexMatch;
import io.github.tgkit.core.annotation.matching.MessageTextMatch;
import io.github.tgkit.core.annotation.matching.UserRoleMatch;
import java.util.Collections;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.inlinequery.InlineQuery;

public class SimpleBotCommands {

  @BotHandler(type = BotRequestType.MESSAGE)
  @MessageTextMatch("ping")
  public BotResponse ping(BotRequest<Message> request) {
    SendMessage send = new SendMessage(request.data().getChatId().toString(), "pong");
    return BotResponse.builder().method(send).build();
  }

  @BotHandler(type = BotRequestType.MESSAGE)
  @MessageContainsMatch("hello")
  public BotResponse hello(BotRequest<Message> request) {
    SendMessage send = new SendMessage(request.data().getChatId().toString(), "Hello!");
    return BotResponse.builder().method(send).build();
  }

  @BotHandler(type = BotRequestType.MESSAGE)
  @MessageRegexMatch(".*\\d+.*")
  public BotResponse numbers(BotRequest<Message> request) {
    SendMessage send = new SendMessage(request.data().getChatId().toString(), "numbers");
    return BotResponse.builder().method(send).build();
  }

  @BotHandler(type = BotRequestType.MESSAGE)
  @UserRoleMatch(
      provider = SimpleRoleProvider.class,
      roles = {"ADMIN"})
  public BotResponse admin(BotRequest<Message> request) {
    SendMessage send = new SendMessage(request.data().getChatId().toString(), "admin");
    return BotResponse.builder().method(send).build();
  }

  @BotHandler(type = BotRequestType.CALLBACK_QUERY)
  @AlwaysMatch
  public BotResponse callback(BotRequest<CallbackQuery> request) {
    AnswerCallbackQuery ans = new AnswerCallbackQuery();
    ans.setCallbackQueryId(request.data().getId());
    ans.setText("callback");
    return BotResponse.builder().method(ans).build();
  }

  @BotHandler(type = BotRequestType.INLINE_QUERY)
  @AlwaysMatch
  public BotResponse inline(BotRequest<InlineQuery> request) {
    AnswerInlineQuery ans = new AnswerInlineQuery();
    ans.setInlineQueryId(request.data().getId());
    ans.setResults(Collections.emptyList());
    return BotResponse.builder().method(ans).build();
  }
}
