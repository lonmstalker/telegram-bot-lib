package io.lonmstalker.tgkit.webhook;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.bot.Bot;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.BotFactory;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.observability.BotObservability;
import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import io.lonmstalker.tgkit.testkit.RecordedRequest;
import io.lonmstalker.tgkit.testkit.TelegramMockServer;
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

/** Tests for {@link WebhookServer}. */
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
              .sender(new io.lonmstalker.tgkit.core.bot.TelegramSender(config, "TOKEN"))
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
              .sender(new io.lonmstalker.tgkit.core.bot.TelegramSender(config, "TOKEN"))
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
