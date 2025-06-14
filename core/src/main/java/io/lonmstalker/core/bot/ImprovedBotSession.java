package io.lonmstalker.core.bot;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class ImprovedBotSession implements BotSession {
    private static final Logger log = LoggerFactory.getLogger(ImprovedBotSession.class);
    private final AtomicBoolean running = new AtomicBoolean();
    private final BlockingQueue<Update> updates = new LinkedBlockingQueue<>();
    private final ObjectMapper mapper = new ObjectMapper();

    private DefaultBotOptions options;
    private String token;
    private LongPollingBot callback;

    private ExecutorService executor;
    private HttpClient httpClient;
    private int lastUpdateId;

    @Override
    public synchronized void start() {
        if (running.get()) {
            throw new IllegalStateException("Session already running");
        }
        if (options == null || token == null || callback == null) {
            throw new IllegalStateException("Session not initialized");
        }
        running.set(true);

        HttpClient.Builder clientBuilder = HttpClient.newBuilder();
        if (options.getProxyHost() != null && !options.getProxyHost().isEmpty()) {
            clientBuilder.proxy(ProxySelector.of(new InetSocketAddress(options.getProxyHost(), options.getProxyPort())));
        }
        this.httpClient = clientBuilder
                .connectTimeout(Duration.ofSeconds(75))
                .build();

        this.executor = Executors.newFixedThreadPool(2);
        executor.execute(this::readLoop);
        executor.execute(this::handleLoop);
    }

    @Override
    public synchronized void stop() {
        if (!running.get()) {
            throw new IllegalStateException("Session already stopped");
        }
        running.set(false);
        executor.shutdownNow();
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void setOptions(BotOptions options) {
        if (this.options != null) {
            throw new IllegalArgumentException("Options already set");
        }
        this.options = (DefaultBotOptions) options;
    }

    @Override
    public void setToken(String token) {
        if (this.token != null) {
            throw new IllegalArgumentException("Token already set");
        }
        this.token = token;
    }

    @Override
    public void setCallback(LongPollingBot callback) {
        if (this.callback != null) {
            throw new IllegalArgumentException("Callback already set");
        }
        this.callback = callback;
    }

    private void readLoop() {
        while (running.get()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(buildUrl()))
                        .timeout(Duration.ofSeconds(options.getGetUpdatesTimeout() + 5))
                        .GET()
                        .build();
                String body = httpClient.send(request, HttpResponse.BodyHandlers.ofString()).body();
                GetUpdatesResponse response = mapper.readValue(body, GetUpdatesResponse.class);
                if (response.result != null && !response.result.isEmpty()) {
                    for (Update update : response.result) {
                        if (update.getUpdateId() > lastUpdateId) {
                            lastUpdateId = update.getUpdateId();
                        }
                    }
                    updates.addAll(response.result);
                }
            } catch (IOException | InterruptedException e) {
                log.warn("Error getting updates: {}", e.getMessage());
                try {
                    Thread.sleep(1000L);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    private void handleLoop() {
        while (running.get()) {
            try {
                Update first = updates.take();
                List<Update> batch = new ArrayList<>();
                batch.add(first);
                updates.drainTo(batch, options.getGetUpdatesLimit() - 1);
                callback.onUpdatesReceived(batch);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private String buildUrl() {
        return "https://api.telegram.org/bot" + token + "/getUpdates?timeout=" + options.getGetUpdatesTimeout() +
                "&limit=" + options.getGetUpdatesLimit() + "&offset=" + (lastUpdateId + 1);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GetUpdatesResponse {
        public boolean ok;
        public List<Update> result;
    }
}
