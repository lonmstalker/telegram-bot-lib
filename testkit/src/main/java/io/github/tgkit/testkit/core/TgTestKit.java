package io.github.tgkit.testkit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tgkit.api.BotAdapter;
import io.github.tgkit.internal.config.BotGlobalConfig;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Небольшой DSL для сценарных тестов Telegram-ботов.
 *
 * <pre>{@code
 * TgTestKit.with(adapter)
 *     .sendText("/ping")
 *     .expectReply("pong")
 *     .end();
 * }</pre>
 */
public final class TgTestKit {

  private final FakeTransport transport;
  private final ObjectMapper mapper = BotGlobalConfig.INSTANCE.http().getMapper();

  private TgTestKit(@NonNull BotAdapter adapter) {
    this.transport = new FakeTransport(adapter);
  }

  /** Создаёт DSL для указанного адаптера. */
  public static @NonNull TgTestKit with(@NonNull BotAdapter adapter) {
    return new TgTestKit(adapter);
  }

  /** Отправляет текстовое сообщение от пользователя с id {@code 1}. */
  public @NonNull TgTestKit sendText(@NonNull String text) {
    Message msg = new Message();
    msg.setText(text);
    msg.setChatId(1L);
    msg.setFrom(new org.telegram.telegrambots.meta.api.objects.User());
    Update update = new Update();
    update.setMessage(msg);
    transport.dispatch(update);
    return this;
  }

  /** Проверяет, что последним отправлен {@link SendMessage} с ожидаемым текстом. */
  public @NonNull TgTestKit expectReply(@NonNull String expected) {
    BotApiMethod<?> method = transport.lastMethod();
    if (!(method instanceof SendMessage m)) {
      throw new AssertionError("Last method is not SendMessage: " + method);
    }
    if (!Objects.equals(expected, m.getText())) {
      throw new AssertionError(
          "Expected text '%s' but was '%s'".formatted(expected, m.getText()));
    }
    return this;
  }

  /** Сравнивает JSON последнего метода с ресурсом в classpath. */
  public @NonNull TgTestKit expectJsonSnapshot(@NonNull String resource)
      throws IOException {
    BotApiMethod<?> method =
        Objects.requireNonNull(transport.lastMethod(), "no method sent");
    String expected;
    try (InputStream in = getClass().getResourceAsStream(resource)) {
      if (in == null) {
        throw new IllegalArgumentException("Resource '" + resource + "' not found");
      }
      expected = new String(in.readAllBytes());
    }
    String actual = mapper.writeValueAsString(method);
    if (!mapper.readTree(expected).equals(mapper.readTree(actual))) {
      throw new AssertionError("JSON differs:\nexpected: " + expected + "\nactual: " + actual);
    }
    return this;
  }

  /** Завершает цепочку. Для совместимости с try-with-resources. */
  public void end() {
    // no-op
  }
}
