# 📦 tgkit-observability
*Набор инструментов наблюдаемости для ботов на базе **tgkit***

---

## Основные классы

| Класс | Что делает |
|-------|------------|
| `ObservabilityInterceptor` | Собирает метрики и трейсы для каждого `Update`, записывает ID в MDC |
| `MetricsCollector` | Абстракция над Micrometer, возвращает `MeterRegistry`, таймеры и счётчики |
| `BotObservability` | Фабрика готовых `Tracer` и `MetricsCollector` (Micrometer/Prometheus, OpenTelemetry) |
| `LogContext` | Утилита для работы с MDC: `put`, `remove`, `clear` |

## Пример подключения

```java
MetricsCollector metrics = BotObservability.micrometer(8081);
Tracer tracer = BotObservability.otelTracer("bot-service");
BotConfig cfg = BotConfig.builder()
        .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
        .build();
Bot bot = BotFactory.INSTANCE.from("TOKEN", cfg, update -> null, "com.example.bot");
bot.start();
```

## Метрики

| Имя | Тип | Описание |
|-----|-----|----------|
| `update_latency_ms` | timer | время обработки обновления |
| `updates_total` | counter | количество успешно обработанных обновлений |
| `update_errors_total` | counter | число ошибок во время обработки |
| `updates_dropped_total` | counter | сколько обновлений отклонено из-за ошибок или переполнения очереди |
| `updates_queue_size` | gauge | текущий размер очереди обновлений/обрабатываемых HTTP-запросов |

