# TgKit — лёгковесный Java-фреймворк для Telegram-ботов

> ⚡ Запуск за ≤ 5 минут, нулевая рефлексия на «горячем» пути и production-grade наблюдаемость из коробки.

---

## ✨ Особенности

| ✔️ | Возможность                                        | Описание                                                                      |
|----|----------------------------------------------------|-------------------------------------------------------------------------------|
| ✅  | **Middleware-конвейер**                            | Гибкая `Interceptor`-цепочка по образцу Spring Web / gRPC                     |
| ✅  | **Аннотационные хендлеры**                         | `@BotHandler`, `@MessageRegexMatch`, `@Arg`, compile-time-проверка            |
| ✅  | **Rate-limit & Retry**                             | Авто-back-off при 429/5xx, лимиты per-user/chat                               |
| ✅  | **Pluggable StateStore**                           | In-memory → Redis → JDBC переключается одной строкой                          |
| ✅  | **Webhook ⇆ Polling auto-failover**                | Метод `serveHybrid()` сам решает, что сейчас живо                             |
| 🟡 | Инструменты тестирования                           | Record/Replay JSON-`Update`, JUnit-rule `@BotTest`                            |
| ✅  | [Метрики / трейсы / логи](observability/README.md) | Micrometer + OpenTelemetry + SLF4J/MDC                                        |
| ✅  | **Плагинная архитектура**                          | Отдельный модуль `plugin` с `ServiceLoader`, hot-reload и системой разрешений |
| 🟡 | [Security-bundle](security/README.md)              | Rate-limit, inline-CAPTCHA, ACL и Spring Boot-стартер                         |
| 🟡 | Расширенный форматтер                              | Markdown V2 / HTML, media-group, шаблоны FreeMarker                           |
| 🟡 | Версионирование API                                | Сканер Bot API, генерация миграционных отчётов                                |
| 🟡 | Data-validation                                    | `@Range`, `@Pattern`, собственные `Converter<?>`                              |
| 🟡 | Inline-Query / Web-App                             | Fluent-DSL + кеширование `file_id`                                            |
| 🟡 | CLI / Admin-бот                                    | `/stats`, `/broadcast`, `bot-cli`                                             |
| 🟡 | Авто-документация                                  | HTML-страница OpenAPI-стиля                                                   |
| 🟡 | Media-streaming                                    | NIO-upload, резюмирование больших файлов                                      |
| ✅  | **Шифрование токена**                              | `TokenCipher` (AES-GCM, ключ 16/32 байта)                                     |
| ✅  | **Локализация**                                    | ICU4J plural-rules, `localizer().get("key")`                                  |
| ✅  | **Поддержка БД**                                   | H2, PostgreSQL, MySQL, Oracle (драйвер — внешне)                              |

🟡 — функция доступна в отдельном модуле; статус следите в Issues/Projects.

---

## 🚀 Быстрый старт

<details>
<summary>Maven</summary>

```xml
<dependency>
    <groupId>io.github.tgkit</groupId>
    <artifactId>core</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>

<!-- Подключение compile-time проверок -->
...
<plugin>
<groupId>org.apache.maven.plugins</groupId>
<artifactId>maven-compiler-plugin</artifactId>
...
<annotationProcessorPaths>
...
    <path>
        <groupId>io.github.tgkit</groupId>
        <artifactId>core</artifactId>
        <version>${project.version}</version>
    </path>
...
<annotationProcessors>
    <annotationProcessor>
        io.github.tgkit.core.processor.BotHandlerProcessor
    </annotationProcessor>
...
</plugin>
```

</details> 
<details>
<summary>Gradle Kotlin DSL</summary>

```kotlin 
implementation("io.github.tgkit:core:0.0.1-SNAPSHOT")
```

</details>

<details>
<summary>TestKit</summary>

```xml
<dependency>
    <groupId>io.github.tgkit</groupId>
    <artifactId>testkit</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

```kotlin
testImplementation("io.github.tgkit:testkit:0.0.1-SNAPSHOT")
```

</details>

<details>
<summary>Spring Boot</summary>

```xml
<dependency>
    <groupId>io.github.tgkit</groupId>
    <artifactId>boot</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

</details>

### 1 — Минимальный эхо-бот

```java
public class EchoCommands {

    @BotHandler(type = BotRequestType.MESSAGE)
    @AlwaysMatch
    public BotResponse echo(BotRequest<Message> req) {
        var msg   = req.data();
        var reply = new SendMessage(msg.getChatId().toString(), msg.getText());
        return BotResponse.builder().method(reply).build();
    }
}

// Конфигурация может быть YAML или JSON
TelegramBot.run(Path.of("bot.yaml"));

```

### 2 — Работа с StateStore

```java
JedisPool pool = new JedisPool("localhost", 6379);
BotConfig cfg = BotConfig.builder()
        .store(new RedisStateStore(pool))    // можно InMemoryStateStore, JdbcStateStore…
        .build();

TelegramBot.run(Path.of("bot.yaml"));

@BotHandler(type = BotRequestType.MESSAGE)
public void quiz(BotRequest<Message> req) {
    var store = req.botInfo().store();
    long chat = req.user().chatId();

    String step = store.get(String.valueOf(chat));     // чтение
    // … бизнес-логика …
    store.set(String.valueOf(chat), "NEXT");           // запись
}
```

### 3 — Observability за 30 секунд

```java
var metrics = MicrometerCollector.prometheus(9180);   // /prometheus, сервер уже запущен
var tracer  = OTelTracer.stdoutDev();                 // спаны в лог

BotConfig cfg = BotConfig.builder()
        .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
        .build();

TelegramBot.run(Path.of("bot.yaml"));

```

### 4 — Юнит-тестирование бота

```java
@TelegramBotTest
class PingCommandTest {

    @Test
    void pingPong(UpdateInjector inject, Expectation expect) {
        inject.text("/ping").from(42L);
        expect.api("sendMessage").jsonPath("$.text", "pong");
    }
}
```

### 5 — Безопасность и наблюдаемость

```java
MetricsCollector metrics = BotObservability.micrometer(9180);
Tracer tracer = BotObservability.otelTracer("sample-bot");
AntiSpamInterceptor guard = BotSecurity.antiSpamInterceptor(Set.of("spam.com"));

BotConfig cfg = BotConfig.builder()
        .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
        .globalInterceptor(guard)
        .build();

Bot bot = BotFactory.INSTANCE.from(token, cfg, update -> null);
bot.start();
```

### 6 — Инициализация ядра и virtual threads

```java
BotGlobalConfig.INSTANCE
        .executors()
        .cpuPoolSize(4)
        .scheduledPoolSize(4);
BotCoreInitializer.init();
```

Virtual threads ("виртуальные потоки") позволяют запускать тысячи задач с минимальными затратами памяти.
Они отлично подходят для сетевых операций, но блокирующий код всё ещё занимает платформенный поток,
поэтому при интенсивных CPU-вычислениях стоит внимательно выбирать размеры пулов.

## 🛠️ Сборка и тесты

Минимальные требования: Java 21 и установленный Maven.

В среде Codex перед запуском Maven экспортируйте переменные прокси:

```bash
export http_proxy=http://proxy:8080
export https_proxy=http://proxy:8080
export MAVEN_OPTS="-Dhttps.proxyHost=proxy -Dhttps.proxyPort=8080 \
-Dhttp.proxyHost=proxy -Dhttp.proxyPort=8080 \
-Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
```

Сборка всех модулей выполняется командой:

```bash
mvn clean install
```

Для запуска только тестов используйте:

```bash
mvn test
```

Для генерации отчёта по покрытию:

```bash
mvn verify jacoco:report
```

Результат ищите в `target/site/jacoco/index.html`.

Для локального сканирования зависимостей на уязвимости:

```bash
mvn verify
```

Отчёты появятся в `target/dependency-check-report.html` и
`target/dependency-check-report.sarif`.

### Бенчмарки

Для запуска JMH-бенчмарков:

```bash
mvn -Pbenchmarks -pl benchmarks test
```

## 🤝 Contributing

PR-ы и идеи приветствуются! Перед отправкой ознакомьтесь с [CONTRIBUTING.md](CONTRIBUTING.md)
и [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

⚖️ Лицензия
Apache License 2.0 © 2025 TgKit Team

История изменений — в [CHANGELOG.md](CHANGELOG.md).

### Pre-commit hook

Для автоматического форматирования и проверки стиля перед коммитом подключите локальный хук:

```bash
ln -s ../../githooks/pre-commit .git/hooks/pre-commit
```

Коммит будет прерван, если `mvn -q checkstyle:check` обнаружит нарушения.
После успешной проверки запускается `mvn -q verify` для сборки и тестов.

* [doc2oas](doc2oas/README.md) — генерация OpenAPI и SDK
