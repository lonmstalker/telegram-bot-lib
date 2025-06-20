/**
 * Инструменты наблюдаемости: метрики и трассировки.
 *
 * <p>Содержит {@link io.lonmstalker.observability.ObservabilityInterceptor} и
 * утилиты из {@link io.lonmstalker.observability.BotObservability}.
 *
 * <p>Пример подключения:
 * <pre>{@code
 * MetricsCollector metrics = BotObservability.micrometer(9180);
 * Tracer tracer = BotObservability.otelTracer("bot-service");
 * BotConfig cfg = BotConfig.builder()
 *         .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
 *         .build();
 * }
 * </pre>
 */
package io.lonmstalker.observability;
