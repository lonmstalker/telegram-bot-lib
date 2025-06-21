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

import static org.assertj.core.api.Assertions.assertThat;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;

class TelegramMockServerTest {

  @Test
  void recordsRequestAndReturnsEnqueuedResponse() throws Exception {
    try (TelegramMockServer server = new TelegramMockServer()) {
      server.enqueue("{\"ok\":true,\"result\":42}");
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request =
          HttpRequest.newBuilder()
              .uri(URI.create(server.baseUrl() + "TEST/sendMessage"))
              .POST(HttpRequest.BodyPublishers.ofString("{}"))
              .build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.body()).contains("\"result\":42");
      RecordedRequest recorded = server.takeRequest(1, TimeUnit.SECONDS);
      assertThat(recorded).isNotNull();
      assertThat(recorded.path()).endsWith("/sendMessage");
      assertThat(recorded.method()).isEqualTo("POST");
      assertThat(recorded.body()).isEqualTo("{}");
    }
  }

  @Test
  void recordsEmptyBodyWhenNoContent() throws Exception {
    try (TelegramMockServer server = new TelegramMockServer()) {
      HttpClient client = HttpClient.newHttpClient();
      HttpRequest request =
          HttpRequest.newBuilder().uri(URI.create(server.baseUrl() + "TEST/getMe")).GET().build();
      HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
      assertThat(response.body()).contains("\"ok\":true");
      RecordedRequest recorded = server.takeRequest(1, TimeUnit.SECONDS);
      assertThat(recorded).isNotNull();
      assertThat(recorded.method()).isEqualTo("GET");
      assertThat(recorded.path()).endsWith("/getMe");
      assertThat(recorded.body()).isEmpty();
    }
  }
}
