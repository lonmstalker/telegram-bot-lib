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
package io.github.tgkit.testkit;

import io.undertow.Undertow;
import io.undertow.util.Headers;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.nio.charset.StandardCharsets;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

/** Минимальный HTTP-сервер, имитирующий Telegram API для тестов. */
public final class TelegramMockServer implements AutoCloseable {

  private final Undertow server;
  private final int port;
  private final BlockingQueue<RecordedRequest> requests = new LinkedBlockingQueue<>();
  private final Queue<String> responses = new LinkedBlockingQueue<>();

  /** Создаёт и запускает сервер на свободном порту. */
  public TelegramMockServer() {
    this.port = findFreePort();
    this.server =
        Undertow.builder()
            .addHttpListener(port, "localhost")
            .setHandler(
                exchange -> {
                  exchange.startBlocking();
                  byte[] bytes = exchange.getInputStream().readAllBytes();
                  String body = bytes.length == 0 ? "" : new String(bytes, StandardCharsets.UTF_8);
                  requests.add(
                      new RecordedRequest(
                          exchange.getRequestMethod().toString(),
                          exchange.getRequestPath(),
                          exchange.getRequestHeaders().getHeaderNames().stream()
                              .collect(
                                  java.util.stream.Collectors.toMap(
                                      Object::toString, h -> exchange.getRequestHeaders().get(h))),
                          body));
                  String response = Objects.requireNonNullElse(responses.poll(), "{\"ok\":true}");
                  exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                  exchange.getResponseSender().send(response);
                })
            .build();
    this.server.start();
  }

  private static int findFreePort() {
    try (ServerSocket socket = new ServerSocket()) {
      socket.bind(new InetSocketAddress(0));
      return socket.getLocalPort();
    } catch (Exception e) {
      throw new IllegalStateException("Failed to allocate port", e);
    }
  }

  /** Базовый URL для передачи в {@link io.github.tgkit.core.bot.BotConfig#setBaseUrl(String)}. */
  public String baseUrl() {
    return "http://localhost:" + port + "/bot";
  }

  /** Добавляет ответ, который будет отправлен при следующем запросе. */
  public void enqueue(String json) {
    responses.add(json);
  }

  /** Возвращает следующий записанный запрос или {@code null}, если таймаут истёк. */
  public RecordedRequest takeRequest(long timeout, TimeUnit unit) throws InterruptedException {
    return requests.poll(timeout, unit);
  }

  @Override
  public void close() {
    server.stop();
  }
}
