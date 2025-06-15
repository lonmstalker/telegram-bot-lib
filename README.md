# TgKit — лёгковесный Java-фреймворк для Telegram-ботов

> ⚡ Запуск за ≤ 5 минут, нулевая рефлексия на «горячем» пути и production-grade наблюдаемость из коробки.

---

## ✨ Особенности

| ✔️ | Возможность | Описание |
|----|-------------|----------|
| ✅ | **Middleware-конвейер** | Гибкая `Interceptor`-цепочка по образцу Spring Web / gRPC |
| ✅ | **Аннотационные хендлеры** | `@BotHandler`, `@MessageRegexMatch`, `@Arg`, compile-time-проверка |
| ✅ | **Rate-limit & Retry** | Авто-back-off при 429/5xx, лимиты per-user/chat |
| ✅ | **Pluggable StateStore** | In-memory → Redis → JDBC переключается одной строкой |
| ✅ | **Webhook ⇆ Polling auto-failover** | Метод `serveHybrid()` сам решает, что сейчас живо |
| 🟡 | Инструменты тестирования | Record/Replay JSON-`Update`, JUnit-rule `@BotTest` |
| ✅ | Метрики / трейсы / логи | Micrometer + OpenTelemetry + SLF4J/MDC |
| ✅ | **Плагинная архитектура** | Отдельный модуль `plugin` с `ServiceLoader`, hot-reload и системой разрешений |
| 🟡 | Security-bundle | Rate-limit, inline-CAPTCHA, ACL-аннотация |
| 🟡 | Расширенный форматтер | Markdown V2 / HTML, media-group, шаблоны FreeMarker |
| 🟡 | Версионирование API | Сканер Bot API, генерация миграционных отчётов |
| 🟡 | Data-validation | `@Range`, `@Pattern`, собственные `Converter<?>` |
| 🟡 | Inline-Query / Web-App | Fluent-DSL + кеширование `file_id` |
| 🟡 | CLI / Admin-бот | `/stats`, `/broadcast`, `bot-cli` |
| 🟡 | Авто-документация | HTML-страница OpenAPI-стиля |
| 🟡 | Media-streaming | NIO-upload, резюмирование больших файлов |
| ✅ | **Шифрование токена** | `TokenCipher` (AES-GCM по умолчанию) |
| ✅ | **Локализация** | ICU4J plural-rules, `localizer().get("key")` |
| ✅ | **Поддержка БД** | H2, PostgreSQL, MySQL, Oracle (драйвер — внешне) |

🟡 — функция доступна в отдельном модуле; статус следите в Issues/Projects.

---

## 🚀 Быстрый старт

<details>
<summary>Maven</summary>

```xml
<dependency>
    <groupId>io.lonmstalker</groupId>
    <artifactId>tgkit</artifactId>
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
        <groupId>io.lonmstalker</groupId>
        <artifactId>core</artifactId>
        <version>${project.version}</version>
    </path>
...
<annotationProcessors>
    <annotationProcessor>
        io.lonmstalker.core.processor.BotHandlerProcessor
    </annotationProcessor>
...
</plugin>
```
</details> 
<details>
<summary>Gradle Kotlin DSL</summary>

```kotlin 
implementation("io.lonmstalker:tgkit:0.0.1-SNAPSHOT")
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
var metrics = MicrometerCollector.prometheus(9180);   // /prometheus
var tracer  = OTelTracer.stdoutDev();                 // спаны в лог

BotConfig cfg = BotConfig.builder()
        .addInterceptor(new ObservabilityInterceptor(metrics, tracer))
        .build();

Bot bot = BotFactory.INSTANCE.from(token, cfg, adapter);

```

## 🛠️ Сборка и тесты

Минимальные требования: Java 21 и установленный Maven.

Сборка всех модулей выполняется командой:

```bash
mvn clean install
```

Для запуска только тестов используйте:

```bash
mvn test
```

## 🤝 Contributing
PR-ы и идеи приветствуются! Перед отправкой ознакомьтесь с CONTRIBUTING.md и CODE_OF_CONDUCT.md.

⚖️ Лицензия
Apache License 2.0 © 2025 TgKit Team