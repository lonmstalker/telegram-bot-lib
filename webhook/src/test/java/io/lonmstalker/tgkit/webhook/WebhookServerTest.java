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

package io.github.tgkit.webhook;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.observability.BotObservability;
import io.github.observability.MetricsCollector;
import io.github.tgkit.core.BotCommand;
import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.BotRequestType;
import io.github.tgkit.core.BotResponse;
import io.github.tgkit.core.bot.Bot;
import io.github.tgkit.core.bot.BotAdapterImpl;
import io.github.tgkit.core.bot.BotConfig;
import io.github.tgkit.core.bot.BotFactory;
import io.github.tgkit.core.config.BotGlobalConfig;
import io.github.tgkit.core.matching.CommandMatch;
import io.github.tgkit.testkit.RecordedRequest;
import io.github.tgkit.testkit.TelegramMockServer;
import io.github.tgkit.testkit.TestBotBootstrap;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

/**
 * Tests for {@link WebhookServer}.
 */
class WebhookServerTest {
  private static final ObjectMapper MAPPER = BotGlobalConfig.INSTANCE.http().getMapper();

  @Test
  void dispatchesUpdateToBotAndAddsHsts() throws Exception {
    BotGlobalConfig.INSTANCE.webhook().engine(WebhookServer.Engine.JETTY).port(0).secret("SECRET");
    MetricsCollector mc = BotObservability.micrometer(0);
    BotGlobalConfig.INSTANCE.observability().collector(mc);
    TestBotBootstrap.initOnce();
    WebhookServer server = BotGlobalConfig.INSTANCE.webhook().server();
    try (TelegramMockServer tgServer = new TelegramMockServer()) {
      tgServer.enqueue("{\"ok\":true,\"result\":{\"id\":1,\"is_bot\":true,\"username\":\"bot\"}}");
      BotConfig config = BotConfig.builder().baseUrl(tgServer.baseUrl()).build();
      BotAdapterImpl adapter =
          BotAdapterImpl.builder()
              .internalId(1L)
              .sender(new io.github.tgkit.core.bot.TelegramSender(config, "TOKEN"))
              .config(config)
              .build();
      adapter.registry().add(new PingCommand());
      var hook = new org.telegram.telegrambots.meta.api.methods.updates.SetWebhook();
      Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, hook);
      hook.setUrl("http://localhost:" + server.port() + "/TOKEN");
      hook.setSecretToken("SECRET");
      bot.start();
      tgServer.takeRequest(1, TimeUnit.SECONDS); // getMe

      Update update = new Update();
      Message msg = new Message();
      msg.setText("/ping");
      Chat chat = new Chat();
      chat.setId(42L);
      msg.setChat(chat);
      User user = new User();
      user.setId(42L);
      msg.setFrom(user);
      update.setMessage(msg);
      update.setUpdateId(1);

      String body = MAPPER.writeValueAsString(update);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:" + server.port() + "/TOKEN"))
              .header("Content-Type", "application/json")
              .header("X-Telegram-Bot-Api-Secret-Token", "SECRET")
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      assertThat(resp.statusCode()).isEqualTo(200);
      assertThat(resp.headers().firstValue("Strict-Transport-Security"))
          .hasValueSatisfying(h -> assertThat(h).contains("max-age"));

      RecordedRequest r = tgServer.takeRequest(1, TimeUnit.SECONDS);
      assertThat(r).isNotNull();
      assertThat(r.path()).endsWith("/sendMessage");
      assertThat(mc.registry().find("updates_dropped_total").counter().count()).isZero();
      assertThat(mc.registry().find("updates_queue_size").gauge().value()).isZero();
      bot.stop();
    }
  }

  @Test
  void dispatchesUpdateToBotAndAddsHstsNetty() throws Exception {
    BotGlobalConfig.INSTANCE.webhook().engine(WebhookServer.Engine.NETTY).port(0).secret("SECRET");
    MetricsCollector mc = BotObservability.micrometer(0);
    BotGlobalConfig.INSTANCE.observability().collector(mc);
    TestBotBootstrap.initOnce();
    WebhookServer server = BotGlobalConfig.INSTANCE.webhook().server();
    try (TelegramMockServer tgServer = new TelegramMockServer()) {
      tgServer.enqueue("{\"ok\":true,\"result\":{\"id\":1,\"is_bot\":true,\"username\":\"bot\"}}");
      BotConfig config = BotConfig.builder().baseUrl(tgServer.baseUrl()).build();
      BotAdapterImpl adapter =
          BotAdapterImpl.builder()
              .internalId(1L)
              .sender(new io.github.tgkit.core.bot.TelegramSender(config, "TOKEN"))
              .config(config)
              .build();
      adapter.registry().add(new PingCommand());
      var hook = new org.telegram.telegrambots.meta.api.methods.updates.SetWebhook();
      Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, hook);
      hook.setUrl("http://localhost:" + server.port() + "/TOKEN");
      hook.setSecretToken("SECRET");
      bot.start();
      tgServer.takeRequest(1, TimeUnit.SECONDS); // getMe

      Update update = new Update();
      Message msg = new Message();
      msg.setText("/ping");
      Chat chat = new Chat();
      chat.setId(42L);
      msg.setChat(chat);
      User user = new User();
      user.setId(42L);
      msg.setFrom(user);
      update.setMessage(msg);
      update.setUpdateId(1);

      String body = MAPPER.writeValueAsString(update);
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:" + server.port() + "/TOKEN"))
              .header("Content-Type", "application/json")
              .header("X-Telegram-Bot-Api-Secret-Token", "SECRET")
              .POST(HttpRequest.BodyPublishers.ofString(body))
              .build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      assertThat(resp.statusCode()).isEqualTo(200);
      assertThat(resp.headers().firstValue("Strict-Transport-Security"))
          .hasValueSatisfying(h -> assertThat(h).contains("max-age"));

      RecordedRequest r = tgServer.takeRequest(1, TimeUnit.SECONDS);
      assertThat(r).isNotNull();
      assertThat(r.path()).endsWith("/sendMessage");
      assertThat(mc.registry().find("updates_dropped_total").counter().count()).isZero();
      assertThat(mc.registry().find("updates_queue_size").gauge().value()).isZero();
      bot.stop();
    }
  }

  @Test
  void rejectsRequestWithWrongToken() throws Exception {
    BotGlobalConfig.INSTANCE.webhook().engine(WebhookServer.Engine.JETTY).port(0).secret("SECRET");
    MetricsCollector mc = BotObservability.micrometer(0);
    BotGlobalConfig.INSTANCE.observability().collector(mc);
    TestBotBootstrap.initOnce();
    WebhookServer server = BotGlobalConfig.INSTANCE.webhook().server();
    try (TelegramMockServer tgServer = new TelegramMockServer()) {
      tgServer.enqueue("{\"ok\":true,\"result\":{\"id\":1,\"is_bot\":true,\"username\":\"bot\"}}");
      BotConfig config = BotConfig.builder().baseUrl(tgServer.baseUrl()).build();
      BotAdapterImpl adapter =
          BotAdapterImpl.builder()
              .internalId(1L)
              .sender(new io.github.tgkit.core.bot.TelegramSender(config, "TOKEN"))
              .config(config)
              .build();
      var hook = new org.telegram.telegrambots.meta.api.methods.updates.SetWebhook();
      Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, hook);
      hook.setUrl("http://localhost:" + server.port() + "/TOKEN");
      hook.setSecretToken("SECRET");
      bot.start();
      tgServer.takeRequest(1, TimeUnit.SECONDS); // getMe

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:" + server.port() + "/TOKEN"))
              .header("X-Telegram-Bot-Api-Secret-Token", "WRONG")
              .POST(HttpRequest.BodyPublishers.ofString("{}"))
              .build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      assertThat(resp.statusCode()).isEqualTo(401);
      RecordedRequest r = tgServer.takeRequest(1, TimeUnit.SECONDS);
      assertThat(r).isNull();
      assertThat(mc.registry().find("updates_dropped_total").counter().count()).isEqualTo(1.0);
      assertThat(mc.registry().find("updates_queue_size").gauge().value()).isZero();
      bot.stop();
    }
  }

  @Test
  void rejectsRequestWithWrongTokenNetty() throws Exception {
    BotGlobalConfig.INSTANCE.webhook().engine(WebhookServer.Engine.NETTY).port(0).secret("SECRET");
    MetricsCollector mc = BotObservability.micrometer(0);
    BotGlobalConfig.INSTANCE.observability().collector(mc);
    TestBotBootstrap.initOnce();
    WebhookServer server = BotGlobalConfig.INSTANCE.webhook().server();
    try (TelegramMockServer tgServer = new TelegramMockServer()) {
      tgServer.enqueue("{\"ok\":true,\"result\":{\"id\":1,\"is_bot\":true,\"username\":\"bot\"}}");
      BotConfig config = BotConfig.builder().baseUrl(tgServer.baseUrl()).build();
      BotAdapterImpl adapter =
          BotAdapterImpl.builder()
              .internalId(1L)
              .sender(new io.github.tgkit.core.bot.TelegramSender(config, "TOKEN"))
              .config(config)
              .build();
      var hook = new org.telegram.telegrambots.meta.api.methods.updates.SetWebhook();
      Bot bot = BotFactory.INSTANCE.from("TOKEN", config, adapter, hook);
      hook.setUrl("http://localhost:" + server.port() + "/TOKEN");
      hook.setSecretToken("SECRET");
      bot.start();
      tgServer.takeRequest(1, TimeUnit.SECONDS); // getMe

      HttpClient client = HttpClient.newHttpClient();
      HttpRequest req =
          HttpRequest.newBuilder()
              .uri(URI.create("http://localhost:" + server.port() + "/TOKEN"))
              .header("X-Telegram-Bot-Api-Secret-Token", "WRONG")
              .POST(HttpRequest.BodyPublishers.ofString("{}"))
              .build();
      HttpResponse<String> resp = client.send(req, HttpResponse.BodyHandlers.ofString());
      assertThat(resp.statusCode()).isEqualTo(401);
      RecordedRequest r = tgServer.takeRequest(1, TimeUnit.SECONDS);
      assertThat(r).isNull();
      assertThat(mc.registry().find("updates_dropped_total").counter().count()).isEqualTo(1.0);
      assertThat(mc.registry().find("updates_queue_size").gauge().value()).isZero();
      bot.stop();
    }
  }

  private static class PingCommand implements BotCommand<Message> {
    @Override
    public BotResponse handle(@NonNull BotRequest<Message> request) {
      SendMessage msg = new SendMessage(request.msg().getChatId().toString(), "pong");
      return BotResponse.builder().method(msg).build();
    }

    @Override
    public @NonNull BotRequestType type() {
      return BotRequestType.MESSAGE;
    }

    @Override
    public @NonNull CommandMatch<Message> matcher() {
      return m -> "/ping".equals(m.getText());
    }
  }
}
