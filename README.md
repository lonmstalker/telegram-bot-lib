# TgKit — лёгковесный Java-фреймворк для Telegram-ботов

> ⚡ Запуск за ≤ 5 минут, нулевая рефлексия на «горячем» пути и production-grade наблюдаемость из коробки.

---

## ✨ Особенности

| ✔️ | Возможность                           | Описание |
|----|---------------------------------------|----------|
| ✅ | **Middleware-конвейер**               | Гибкая `Interceptor`-цепочка по образцу Spring Web / gRPC |
| ✅ | **Аннотационные хендлеры**            | `@BotHandler`, `@MessageRegexMatch`, `@Arg`, compile-time-проверка |
| ✅ | **Rate-limit & Retry**                | Авто-back-off при 429/5xx, лимиты per-user/chat |
| ✅ | **Pluggable StateStore**              | In-memory → Redis → JDBC переключается одной строкой |
| ✅ | **Webhook ⇆ Polling auto-failover**   | Метод `serveHybrid()` сам решает, что сейчас живо |
| 🟡 | Инструменты тестирования              | Record/Replay JSON-`Update`, JUnit-rule `@BotTest` |
| ✅ | [Метрики / трейсы / логи](observability/README.md) | Micrometer + OpenTelemetry + SLF4J/MDC |
| ✅ | **Плагинная архитектура**             | Отдельный модуль `plugin` с `ServiceLoader`, hot-reload и системой разрешений |
| 🟡 | [Security-bundle](security/README.md) | Rate-limit, inline-CAPTCHA, ACL и Spring Boot-стартер |
| 🟡 | Расширенный форматтер                 | Markdown V2 / HTML, media-group, шаблоны FreeMarker |
| 🟡 | Версионирование API                   | Сканер Bot API, генерация миграционных отчётов |
| 🟡 | Data-validation                       | `@Range`, `@Pattern`, собственные `Converter<?>` |
| 🟡 | Inline-Query / Web-App                | Fluent-DSL + кеширование `file_id` |
| 🟡 | CLI / Admin-бот                       | `/stats`, `/broadcast`, `bot-cli` |
| 🟡 | Авто-документация                     | HTML-страница OpenAPI-стиля |
| 🟡 | Media-streaming                       | NIO-upload, резюмирование больших файлов |
| ✅ | **Шифрование токена**                 | `TokenCipher` (AES-GCM по умолчанию) |
| ✅ | **Локализация**                       | ICU4J plural-rules, `localizer().get("key")` |
| ✅ | **Поддержка БД**                      | H2, PostgreSQL, MySQL, Oracle (драйвер — внешне) |

🟡 — функция доступна в отдельном модуле; статус следите в Issues/Projects.

---

## 🚀 Быстрый старт

<details>
<summary>Maven</summary>

```xml
<dependency>
    <groupId>io.lonmstalker.tgkit</groupId>
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
        <groupId>io.lonmstalker.tgkit</groupId>
        <artifactId>core</artifactId>
        <version>${project.version}</version>
    </path>
...
<annotationProcessors>
    <annotationProcessor>
        io.lonmstalker.tgkit.core.processor.BotHandlerProcessor
    </annotationProcessor>
...
</plugin>
```
</details> 
<details>
<summary>Gradle Kotlin DSL</summary>

```kotlin 
implementation("io.lonmstalker.tgkit:core:0.0.1-SNAPSHOT")
```
</details>

<details>
<summary>TestKit</summary>

```xml
<dependency>
    <groupId>io.lonmstalker.tgkit</groupId>
    <artifactId>testkit</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

```kotlin
testImplementation("io.lonmstalker.tgkit:testkit:0.0.1-SNAPSHOT")
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

Bot bot = BotFactory.INSTANCE.from(
        token,
        BotConfig.builder().build(),
        update -> null,                       // вся логика в аннотированных хендлерах
        "com.example.bot");
bot.start();          // smart Webhook ↔︎ Polling

```

### 2 — Работа с StateStore
```java
BotConfig cfg = BotConfig.builder()
        .store(new InMemoryStateStore())    // можно RedisStateStore, JdbcStateStore…
        .build();

Bot bot = BotFactory.INSTANCE.from(token, cfg, adapter);

@BotHandler(type = BotRequestType.MESSAGE)
public void quiz(BotRequest<Message> req) {
    var store = req.botInfo().store();
    long chat = req.user().chatId();

    String step = store.get(chat, "step");            // чтение
    // … бизнес-логика …
    store.set(chat, "step", "NEXT");                  // запись
}
```

### 3 — Observability за 30 секунд
```java
var metrics = MicrometerCollector.prometheus(9180);   // /prometheus, сервер уже запущен
var tracer  = OTelTracer.stdoutDev();                 // спаны в лог

BotConfig cfg = BotConfig.builder()
        .globalInterceptor(new ObservabilityInterceptor(metrics, tracer))
        .build();

Bot bot = BotFactory.INSTANCE.from(token, cfg, adapter);

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

Без них сборка может завершиться ошибкой «Network is unreachable».


Для запуска Maven не требуется предварительная установка: в репозиторий входит Maven Wrapper. Сборку лучше пускать через `./mvnw`.

Сборка всех модулей выполняется командой:

```bash
./mvnw clean install
```

Для запуска только тестов используйте:

```bash
./mvnw test
```

Для генерации отчёта по покрытию:

```bash
./mvnw verify jacoco:report
```
Результат ищите в `target/site/jacoco/index.html`.

## 🤝 Contributing
PR-ы и идеи приветствуются! Перед отправкой ознакомьтесь с [CONTRIBUTING.md](CONTRIBUTING.md) и [CODE_OF_CONDUCT.md](CODE_OF_CONDUCT.md).

⚖️ Лицензия
Apache License 2.0 © 2025 TgKit Team

### Pre-commit hook
Для автоматического форматирования и проверки стиля перед коммитом подключите локальный хук:
```bash
ln -s ../../githooks/pre-commit .git/hooks/pre-commit
```
Коммит будет прерван, если `./mvnw -q checkstyle:check` обнаружит нарушения.
После успешной проверки запускается `./mvnw -q verify` для сборки и тестов.
