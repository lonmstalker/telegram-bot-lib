package io.github.tgkit.testkit.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.reactivestreams.Publisher;
import org.telegram.telegrambots.meta.api.objects.Update;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

/**
 * Простая транспортная прослойка для тестов. Позволяет отправлять {@link Update}
 * и отслеживать вызовы к Telegram API.
 */
public final class FakeTransport {

  private final Sinks.Many<Update> updateSink = Sinks.many().unicast().onBackpressureBuffer();
  private final Sinks.Many<BotMethod<?>> methodSink = Sinks.many().unicast().onBackpressureBuffer();

  /** Получает поток входящих обновлений. */
  public @NonNull Flux<Update> updates() {
    return updateSink.asFlux();
  }

  /** Получает поток отправляемых методов. */
  public @NonNull Flux<BotMethod<?>> methods() {
    return methodSink.asFlux();
  }

  /** Публикует новое обновление. */
  public void emit(@NonNull Update update) {
    updateSink.tryEmitNext(update);
  }

  /** Записывает вызов Telegram API. */
  public void record(@NonNull BotMethod<?> method) {
    methodSink.tryEmitNext(method);
  }

  void complete() {
    updateSink.tryEmitComplete();
    methodSink.tryEmitComplete();
  }
}
