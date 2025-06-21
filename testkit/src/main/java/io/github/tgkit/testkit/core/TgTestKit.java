package io.github.tgkit.testkit.core;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.github.tgkit.api.BotAdapter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Objects;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.Disposable;

/**
 * DSL для удобного тестирования ботов. Использует {@link FakeTransport} как ис
 * точник данных.
 */
public final class TgTestKit {

  private final FakeTransport transport = new FakeTransport();
  private final Disposable subscription;
  private final ObjectMapper mapper = new ObjectMapper();

  public TgTestKit(@NonNull BotAdapter adapter) {
    subscription =
        transport
            .updates()
            .concatMap(
                u ->
                    reactor.core.publisher.Mono.fromCallable(
                            () -> adapter.handle(u))
                        .doOnNext(
                            m -> {
                              if (m != null) {
                                transport.record(new BotMethod<>(m));
                              }
                            }))
            .subscribe();
  }

  /** Отправляет текстовое сообщение от указанного пользователя. */
  public @NonNull TgTestKit sendText(long chatId, @NonNull String text) {
    Message msg = new Message();
    msg.setText(text);
    Chat chat = new Chat();
    chat.setId(chatId);
    msg.setChat(chat);
    Update update = new Update();
    update.setMessage(msg);
    transport.emit(update);
    return this;
  }

  /** Проверяет текст ответа. */
  public @NonNull TgTestKit expectReply(@NonNull String text) {
    BotMethod<?> method =
        transport.methods().blockFirst(Duration.ofSeconds(1));
    Assertions.assertNotNull(method, "No reply emitted");
    BotApiMethod<?> raw = method.method();
    Assertions.assertTrue(raw instanceof SendMessage, "Unexpected method " + raw.getClass());
    Assertions.assertEquals(text, ((SendMessage) raw).getText());
    return this;
  }

  /** Сравнивает сериализованный ответ с JSON из ресурсов. */
  public @NonNull TgTestKit expectJsonSnapshot(@NonNull String resourcePath) throws Exception {
    BotMethod<?> method =
        transport.methods().blockFirst(Duration.ofSeconds(1));
    Assertions.assertNotNull(method, "No reply emitted");
    String actual = mapper.writeValueAsString(method.method());
    var is = TgTestKit.class.getClassLoader().getResourceAsStream(resourcePath);
    Objects.requireNonNull(is, "Snapshot " + resourcePath + " not found");
    String expected = new String(is.readAllBytes(), StandardCharsets.UTF_8);
    Assertions.assertEquals(expected.trim(), actual);
    return this;
  }

  /** Завершает работу тестового конвейера. */
  public void end() {
    transport.complete();
    subscription.dispose();
  }
}
