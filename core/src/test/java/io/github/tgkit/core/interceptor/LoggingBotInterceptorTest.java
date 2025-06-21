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
package io.github.tgkit.internal.interceptor;

import static org.junit.jupiter.api.Assertions.*;

import ch.qos.logback.classic.Level;
import ch.qos.logback.classic.Logger;
import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.read.ListAppender;
import io.github.tgkit.testkit.TestBotBootstrap;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

public class LoggingBotInterceptorTest {
  static {
    TestBotBootstrap.initOnce();
  }

  private final Logger logger = (Logger) LoggerFactory.getLogger(LoggingBotInterceptor.class);
  private final ListAppender<ILoggingEvent> appender = new ListAppender<>();

  @AfterEach
  void tearDown() {
    logger.detachAppender(appender);
  }

  @Test
  void logs_user_and_message_id_on_error() {
    appender.start();
    logger.addAppender(appender);
    LoggingBotInterceptor interceptor = new LoggingBotInterceptor();
    Update update = new Update();
    Message msg = new Message();
    msg.setMessageId(5);
    User user = new User();
    user.setId(10L);
    msg.setFrom(user);
    update.setMessage(msg);
    interceptor.afterCompletion(update, Mockito.mock(), null, new RuntimeException("boom"));
    assertFalse(appender.list.isEmpty());
    ILoggingEvent event = appender.list.get(0);
    assertEquals(Level.DEBUG, event.getLevel());
    Update logged = (Update) event.getArgumentArray()[0];
    assertEquals(5, logged.getMessage().getMessageId());
    assertEquals(10L, logged.getMessage().getFrom().getId());
  }
}
