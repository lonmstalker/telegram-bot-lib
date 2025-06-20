package io.lonmstalker.observability;

import io.lonmstalker.observability.impl.CompositeTracer;
import io.lonmstalker.observability.impl.MicrometerCollector;
import io.lonmstalker.observability.impl.NoOpTracer;
import io.lonmstalker.observability.impl.OTelTracer;
import io.lonmstalker.tgkit.observability.Tracer;
import io.micrometer.prometheusmetrics.PrometheusConfig;
import java.util.Arrays;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Фабрика компонентов наблюдаемости: трейсеры и сборщики метрик.
 *
 * <p>Типичное использование:
 *
 * <pre>{@code
 * Tracer tracer = BotObservability.otelTracer("bot-service");
 * MetricsCollector metrics = BotObservability.micrometer(8081);
 * }</pre>
 */
public final class BotObservability {

  private BotObservability() {}

  /** Возвращает {@link Tracer}, который ничего не делает. */
  public static @NonNull Tracer noopTracer() {
    return new NoOpTracer();
  }

  /** Создаёт OpenTelemetry-трейсер для указанного сервиса. */
  public static @NonNull Tracer otelTracer(@NonNull String serviceName) {
    return new OTelTracer.Builder().serviceName(serviceName).build();
  }

  /** Объединяет несколько трейсеров в один. */
  public static @NonNull Tracer compositeTracer(@NonNull Tracer... tracers) {
    return new CompositeTracer(Arrays.asList(tracers));
  }

  /** Запускает Prometheus-сервер на указанном порту c настройками по умолчанию. */
  public static @NonNull MetricsCollector micrometer(int port) {
    return MicrometerCollector.prometheus(PrometheusConfig.DEFAULT, port);
  }

  /** Запускает Prometheus-сервер с переданной конфигурацией Micrometer. */
  public static @NonNull MetricsCollector micrometer(@NonNull PrometheusConfig cfg, int port) {
    return MicrometerCollector.prometheus(cfg, port);
  }
}
