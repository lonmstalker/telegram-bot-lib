package io.lonmstalker.tgkit.core.bot;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.ProxySelector;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.bots.DefaultBotOptions;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.generics.BotOptions;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.meta.generics.LongPollingBot;

/**
 * Сессия бота, получающая обновления через HTTP long polling и передающая их реализованному {@link
 * LongPollingBot}.
 *
 * <p>Класс поддерживает два фоновых цикла:
 *
 * <ul>
 *   <li>read loop отправляет запрос {@code getUpdates} и кладёт полученные обновления в очередь
 *   <li>handle loop берёт их из очереди и передаёт в бот
 * </ul>
 *
 * Циклы работают на переданном или стандартном executor.
 *
 * <p>Жизненный цикл:
 *
 * <ol>
 *   <li>создайте сессию, укажите опции, токен и callback
 *   <li>однократно вызовите {@link #start()} для запуска опроса
 *   <li>завершите работу через {@link #stop()}
 * </ol>
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * ExecutorService executor = Executors.newSingleThreadExecutor();
 * BotSessionImpl session = new BotSessionImpl(executor, new ObjectMapper());
 * DefaultBotOptions opts = new DefaultBotOptions();
 * session.setOptions(opts);
 * session.setToken("123456:ABC-DEF");
 * session.setCallback(new MyLongPollingBot(opts));
 * session.start();
 * // ...
 * session.stop();
 * executor.shutdown();
 * }</pre>
 */
@SuppressWarnings({"dereference.of.nullable", "argument"})
public class BotSessionImpl implements BotSession {
  private static final Logger log = LoggerFactory.getLogger(BotSessionImpl.class);
  private static final long ENQUEUE_TIMEOUT_MS = 100L;

  private final AtomicBoolean running = new AtomicBoolean();
  private final BlockingQueue<Update> updates;
  private final int queueCapacity;

  private final ObjectMapper mapper;
  private final @Nullable ExecutorService providedExecutor;

  private @Nullable DefaultBotOptions options;
  private @Nullable String token;
  private @Nullable LongPollingBot callback;

  private @Nullable ExecutorService executor;
  private @Nullable HttpClient httpClient;
  private int lastUpdateId;

  public BotSessionImpl() {
    this(null, null, BotConfig.DEFAULT_UPDATE_QUEUE_CAPACITY);
  }

  public BotSessionImpl(@Nullable ExecutorService executor, @Nullable ObjectMapper mapper) {
    this(executor, mapper, BotConfig.DEFAULT_UPDATE_QUEUE_CAPACITY);
  }

  public BotSessionImpl(
      @Nullable ExecutorService executor, @Nullable ObjectMapper mapper, int queueCapacity) {
    this.providedExecutor = executor;
    this.mapper = mapper != null ? mapper : new ObjectMapper();
    this.queueCapacity = queueCapacity;
    this.updates = new LinkedBlockingQueue<>(queueCapacity);
  }

  /**
   * Добавляет обновление в очередь, ожидая освободившееся место не дольше {@code
   * ENQUEUE_TIMEOUT_MS} миллисекунд.
   *
   * @param update обновление Telegram
   * @return {@code true}, если событие помещено в очередь, иначе {@code false}
   */
  boolean enqueueUpdate(Update update) {
    try {
      if (updates.offer(update, ENQUEUE_TIMEOUT_MS, TimeUnit.MILLISECONDS)) {
        return true;
      }
      log.warn(
          "updates queue full ({}), unable to enqueue after {}ms",
          queueCapacity,
          ENQUEUE_TIMEOUT_MS);
      return false;
    } catch (InterruptedException ex) {
      Thread.currentThread().interrupt();
      return false;
    }
  }

  @Override
  public synchronized void start() {
    if (running.get()) {
      throw new IllegalStateException("Session already running");
    }
    Objects.requireNonNull(options, "Options not set");
    Objects.requireNonNull(token, "Token not set");
    Objects.requireNonNull(callback, "Callback not set");

    if (options.getProxyHost() != null && !options.getProxyHost().isEmpty()) {
      this.httpClient =
          HttpClient.newBuilder()
              .proxy(
                  ProxySelector.of(
                      new InetSocketAddress(options.getProxyHost(), options.getProxyPort())))
              .build();
    } else {
      this.httpClient = BotGlobalConfig.INSTANCE.http().getClient();
    }

    this.executor =
        providedExecutor != null
            ? providedExecutor
            : BotGlobalConfig.INSTANCE.executors().getIoExecutorService();
    executor.execute(this::readLoop);
    executor.execute(this::handleLoop);

    running.set(true);
  }

  @Override
  public synchronized void stop() {
    if (!running.get()) {
      throw new IllegalStateException("Session already stopped");
    }
    running.set(false);
    if (executor != null && providedExecutor == null) {
      executor.shutdown();
      try {
        if (!executor.awaitTermination(5, TimeUnit.SECONDS)) {
          executor.shutdownNow();
        }
      } catch (InterruptedException e) {
        Thread.currentThread().interrupt();
      }
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
  public synchronized void setOptions(BotOptions options) {
    if (this.options != null) {
      throw new BotApiException("Options already set");
    }
    this.options = (DefaultBotOptions) options;
  }

  @Override
  public synchronized void setToken(String token) {
    if (this.token != null) {
      throw new BotApiException("Token already set");
    }
    this.token = token;
  }

  @Override
  public synchronized void setCallback(LongPollingBot callback) {
    if (this.callback != null) {
      throw new BotApiException("Callback already set");
    }
    this.callback = callback;
  }

  @SuppressWarnings("argument")
  private void readLoop() {
    scheduleRead(1);
  }

  @SuppressWarnings("argument")
  void scheduleRead(long backOff) {
    if (!running.get()) {
      return;
    }
    BotGlobalConfig.INSTANCE
        .executors()
        .getScheduledExecutorService()
        .schedule(() -> sendRequest(backOff), backOff, TimeUnit.SECONDS);
  }

  @SuppressWarnings("argument")
  private void sendRequest(long backOff) {
    if (!running.get()) {
      return;
    }
    HttpRequest request =
        HttpRequest.newBuilder()
            .uri(URI.create(buildUrl()))
            .timeout(Duration.ofSeconds(Objects.requireNonNull(options).getGetUpdatesTimeout() + 5))
            .GET()
            .build();
    Objects.requireNonNull(httpClient)
        .sendAsync(request, HttpResponse.BodyHandlers.ofInputStream())
        .thenApply(HttpResponse::body)
        .thenAccept(
            stream -> {
              try (InputStream input = stream) {
                GetUpdatesResponse response = mapper.readValue(input, GetUpdatesResponse.class);
                if (response.result != null && !response.result.isEmpty()) {
                  for (Update update : response.result) {
                    if (update.getUpdateId() > lastUpdateId) {
                      lastUpdateId = update.getUpdateId();
                      enqueueUpdate(update);
                    }
                  }
                }
                scheduleRead(1);
              } catch (Exception ex) {
                scheduleRead(handleError(ex, backOff));
              }
            })
        .exceptionally(
            ex -> {
              Throwable cause = ex.getCause() != null ? ex.getCause() : ex;
              scheduleRead(handleError(cause, backOff));
              return null;
            });
  }

  long handleError(Throwable ex, long backOff) {
    log.warn("Error in readLoop: {}, backing off {}s", ex.getMessage(), backOff);
    return Math.min(backOff * 2, 30);
  }

  @SuppressWarnings("argument")
  private void handleLoop() {
    while (running.get() || !updates.isEmpty()) {
      try {
        Update u = updates.poll(1, TimeUnit.SECONDS);
        if (u != null) {
          Objects.requireNonNull(callback).onUpdatesReceived(List.of(u));
        }
      } catch (InterruptedException ex) {
        Thread.currentThread().interrupt();
      }
    }
  }

  @SuppressWarnings("argument")
  private String buildUrl() {
    return String.format(
        "https://api.telegram.org/bot%s/getUpdates?timeout=%d&limit=%d&offset=%d",
        token,
        Objects.requireNonNull(options).getGetUpdatesTimeout(),
        options.getGetUpdatesLimit(),
        (lastUpdateId + 1));
  }

  @JsonIgnoreProperties(ignoreUnknown = true)
  private static class GetUpdatesResponse {
    public boolean ok;
    public List<Update> result = List.of();
  }
}
