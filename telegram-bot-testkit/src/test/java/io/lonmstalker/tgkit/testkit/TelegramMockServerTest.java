package io.lonmstalker.tgkit.testkit;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.concurrent.TimeUnit;

class TelegramMockServerTest {

    @Test
    void recordsRequestAndReturnsEnqueuedResponse() throws Exception {
        try (TelegramMockServer server = new TelegramMockServer()) {
            server.enqueue("{\"ok\":true,\"result\":42}");
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
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
}
