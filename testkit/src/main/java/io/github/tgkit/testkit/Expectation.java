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
import io.github.tgkit.internal.config.BotGlobalConfig;
import java.util.concurrent.TimeUnit;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.junit.jupiter.api.Assertions;

/**
 * Utility for verifying calls to Telegram API in tests.
 *
 * <p>Example usage:
 *
 * <pre>{@code
 * inject.text("/ping").from(1L).dispatch();
 * expect.api("sendMessage").jsonPath("$.text", "pong");
 * }</pre>
 */
public final class Expectation {

  private final TelegramMockServer server;
  private final ObjectMapper mapper = BotGlobalConfig.INSTANCE.http().getMapper();

  Expectation(@NonNull TelegramMockServer server) {
    this.server = server;
  }

  /** Returns expectation for the next API call with given method name. */
  public ApiExpectation api(@NonNull String method) {
    return new ApiExpectation(method);
  }

  /** Checks JSON body of the recorded request. */
  public final class ApiExpectation {
    private final String method;

    ApiExpectation(String method) {
      this.method = method;
    }

    /** Asserts that JSON at {@code path} equals {@code expected}. */
    public void jsonPath(@NonNull String path, @NonNull String expected) {
      try {
        RecordedRequest req = server.takeRequest(1, TimeUnit.SECONDS);
        Assertions.assertNotNull(req, "No request captured");
        Assertions.assertTrue(
            req.path().endsWith("/" + method),
            "Expected method " + method + " but was " + req.path());
        JsonNode body = mapper.readTree(req.body());
        JsonNode node = body.at(toPointer(path));
        Assertions.assertFalse(
            node.isMissingNode(), "Path " + path + " not found in " + req.body());
        Assertions.assertEquals(expected, node.asText());
      } catch (Exception e) {
        throw new AssertionError("Failed to check request", e);
      }
    }

    private String toPointer(String jsonPath) {
      if (jsonPath.startsWith("$")) {
        String p = jsonPath.substring(1);
        if (p.startsWith(".")) {
          p = p.substring(1);
        }
        return "/" + p.replace(".", "/");
      }
      return jsonPath;
    }
  }
}
