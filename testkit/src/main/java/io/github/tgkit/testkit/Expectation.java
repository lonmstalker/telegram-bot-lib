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

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.IOException;
import java.util.Objects;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Утилита для проверки запросов к {@link TelegramMockServer}.
 *
 * <p>Используется в тестах вместе с {@link BotTestExtension}:
 *
 * <pre>{@code
 * @TelegramBotTest
 * class PingCommandTest {
 *   @Test
 *   void pingPong(UpdateInjector inject, Expectation expect) {
 *     inject.text("/ping").from(42L);
 *     expect.api("sendMessage").jsonPath("$.text", "pong");
 *   }
 * }
 * }</pre>
 */
public final class Expectation {

  private static final ObjectMapper MAPPER = new ObjectMapper();
  private static final long TIMEOUT_MS = 1000L;

  private final TelegramMockServer server;

  public Expectation(@NonNull TelegramMockServer server) {
    this.server = server;
  }

  /**
   * Ожидает вызов метода Telegram Bot API.
   *
   * @param method имя метода, например {@code sendMessage}
   * @return объект для дополнительных проверок
   * @throws AssertionError если запрос не получен или метод не совпал
   */
  public @NonNull RequestExpectation api(@NonNull String method) {
    RecordedRequest req;
    try {
      req = server.takeRequest(TIMEOUT_MS, TimeUnit.MILLISECONDS);
    } catch (InterruptedException e) {
      Thread.currentThread().interrupt();
      throw new AssertionError("Interrupted while waiting for request", e);
    }
    if (req == null) {
      throw new AssertionError("No request received for method " + method);
    }
    if (!req.path().endsWith("/" + method)) {
      throw new AssertionError("Expected method %s but got %s".formatted(method, req.path()));
    }
    return new RequestExpectation(req);
  }

  /** Проверки содержимого запроса. */
  public static final class RequestExpectation {
    private final RecordedRequest request;

    private RequestExpectation(RecordedRequest request) {
      this.request = request;
    }

    /**
     * Проверяет значение по выражению JSONPath вида {@code $.field.subfield}.
     *
     * @param expr выражение
     * @param expected ожидаемое значение
     * @return этот же объект для цепочек вызовов
     * @throws AssertionError если значение не совпало или JSON некорректен
     */
    public @NonNull RequestExpectation jsonPath(@NonNull String expr, @NonNull String expected) {
      String pointer = toPointer(expr);
      try {
        JsonNode node = MAPPER.readTree(request.body());
        JsonNode val = node.at(pointer);
        String actual = val.isMissingNode() ? null : val.asText();
        if (!Objects.equals(expected, actual)) {
          throw new AssertionError(
              "Expected %s at %s but was %s".formatted(expected, expr, actual));
        }
      } catch (IOException e) {
        throw new AssertionError("Failed to parse JSON body", e);
      }
      return this;
    }

    private static String toPointer(String expr) {
      if (!expr.startsWith("$")) {
        throw new IllegalArgumentException("Expression must start with '$'");
      }
      if (expr.equals("$")) {
        return "";
      }
      String trimmed = expr.startsWith("$.") ? expr.substring(2) : expr.substring(1);
      return "/" + trimmed.replace('.', '/');
    }
  }
}
