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
package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.github.tgkit.testkit.TestBotBootstrap;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

class QuizBuilderEdgeTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void quizCorrectIdAndValidation() throws TelegramApiException {
    TelegramSender sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<PartialBotApiMethod<?>>any());

    DSLContext ctx = MockCtx.ctx(77L, 88L, sender);

    var qb = new QuizBuilder(ctx, "2+2=?", 1).option("3").option("4").option("5");

    SendPoll poll = (SendPoll) qb.build();

    assertThat(poll.getType()).isEqualTo("quiz");
    assertThat(poll.getCorrectOptionId()).isEqualTo(1);
    assertThat(poll.getChatId()).isEqualTo("77");
  }

  @Test
  void quizWithoutChatThrows() throws TelegramApiException {
    TelegramSender sender = mock(TelegramSender.class);
    doReturn(null).when(sender).execute(Mockito.<PartialBotApiMethod<?>>any());

    DSLContext ctx = MockCtx.ctx(/* chat= */ null, 99L, sender);

    var qb = BotDSL.quiz(ctx, "q", 0).missingIdStrategy(MissingIdStrategy.ERROR);

    assertThatThrownBy(qb::build).isInstanceOfAny(RuntimeException.class, BotApiException.class);
  }
}
