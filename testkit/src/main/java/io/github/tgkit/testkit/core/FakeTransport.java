package io.github.tgkit.testkit.core;

import io.github.tgkit.api.BotAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Простая реализация транспорта для unit-тестов.
 * Получает {@link Update} и передаёт их в {@link BotAdapter},
 * сохраняя ответы типа {@link BotApiMethod}.
 *
 * <p>Не выполняет сетевых запросов и пригоден для отладки логики обработчиков.
 */
public final class FakeTransport {

  private final BotAdapter adapter;
  private final List<Update> received = new ArrayList<>();
  private final List<BotApiMethod<?>> sent = new ArrayList<>();

  public FakeTransport(@NonNull BotAdapter adapter) {
    this.adapter = adapter;
  }

  /**
   * Передать обновление в бота.
   *
   * @param update событие Telegram
   */
  public void dispatch(@NonNull Update update) {
    received.add(update);
    try {
      BotApiMethod<?> method = adapter.handle(update);
      if (method != null) {
        sent.add(method);
      }
    } catch (Exception e) {
      throw new IllegalStateException("Handler failed", e);
    }
  }

  /** Возвращает все полученные обновления. */
  public @NonNull List<Update> updates() {
    return Collections.unmodifiableList(received);
  }

  /** Возвращает все отправленные методы Telegram API. */
  public @NonNull List<BotApiMethod<?>> methods() {
    return Collections.unmodifiableList(sent);
  }

  /** Последний отправленный метод или {@code null}, если ответов не было. */
  public @Nullable BotApiMethod<?> lastMethod() {
    return sent.isEmpty() ? null : sent.get(sent.size() - 1);
  }
}
