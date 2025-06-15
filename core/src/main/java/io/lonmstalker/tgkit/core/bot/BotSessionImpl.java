package io.lonmstalker.tgkit.core.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import io.lonmstalker.tgkit.core.exception.BotApiException;
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
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Slf4j
@SuppressWarnings("dereference.of.nullable")
public class BotSessionImpl implements BotSession {

    private final AtomicBoolean running = new AtomicBoolean();
    private final BlockingQueue<Update> updates = new LinkedBlockingQueue<>();

    private final ObjectMapper mapper;
    private final @Nullable ExecutorService providedExecutor;

    private @Nullable DefaultBotOptions options;
    private @Nullable String token;
    private @Nullable LongPollingBot callback;

    private @Nullable ExecutorService executor;
    private @Nullable HttpClient httpClient;
    private int lastUpdateId;

    public BotSessionImpl() {
        this(null, null);
    }

    public BotSessionImpl(@Nullable ExecutorService executor, @Nullable ObjectMapper mapper) {
        this.providedExecutor = executor;
        this.mapper = mapper != null ? mapper : new ObjectMapper();
    }

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

        this.executor = providedExecutor != null ? providedExecutor : Executors.newVirtualThreadPerTaskExecutor();
        executor.execute(this::readLoop);
        executor.execute(this::handleLoop);
    }

    @Override
    public synchronized void stop() {
        if (!running.get()) {
            throw new IllegalStateException("Session already stopped");
        }
        running.set(false);
        if (executor != null && providedExecutor == null) {
            executor.shutdownNow();
            if (executor instanceof AutoCloseable closable) {
                try {
                    closable.close();
                } catch (Exception e) {
                    log.warn("Error closing executor: {}", e.getMessage());
                }
            }
        }
        if (httpClient != null) {
            httpClient.close();
        }
    }

    @Override
    public boolean isRunning() {
        return running.get();
    }

    @Override
    public void setOptions(BotOptions options) {
        if (this.options != null) {
            throw new BotApiException("Options already set");
        }
        this.options = (DefaultBotOptions) options;
    }

    @Override
    public void setToken(String token) {
        if (this.token != null) {
            throw new BotApiException("Token already set");
        }
        this.token = token;
    }

    @Override
    public void setCallback(LongPollingBot callback) {
        if (this.callback != null) {
            throw new BotApiException("Callback already set");
        }
        this.callback = callback;
    }

    @SuppressWarnings("argument")
    private void readLoop() {
        while (running.get()) {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(buildUrl()))
                        .timeout(Duration.ofSeconds(Objects.requireNonNull(options).getGetUpdatesTimeout() + 5))
                        .GET()
                        .build();
                String body = Objects.requireNonNull(httpClient)
                        .send(request, HttpResponse.BodyHandlers.ofString()).body();
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

    @SuppressWarnings("argument")
    private void handleLoop() {
        while (running.get()) {
            try {
                Update first = updates.take();
                List<Update> batch = new ArrayList<>();
                batch.add(first);
                updates.drainTo(batch, Objects.requireNonNull(options).getGetUpdatesLimit() - 1);
                Objects.requireNonNull(callback).onUpdatesReceived(batch);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    @SuppressWarnings("argument")
    private String buildUrl() {
        return "https://api.telegram.org/bot" + token + "/getUpdates?timeout=" +
                Objects.requireNonNull(options).getGetUpdatesTimeout() +
                "&limit=" + options.getGetUpdatesLimit() + "&offset=" + (lastUpdateId + 1);
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GetUpdatesResponse {
        public boolean ok;
        public List<Update> result = List.of();
    }
}
