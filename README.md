**features**
1. Middleware-конвейер (filter / interceptor chain) [+]
2. Аннотационные хендлеры [+]
3. Rate limit + back-off / retry при ответах/запросах [+]
4. Встроенный state‑store с pluggable back‑end [+]
5. Автоматическое управление webhook / long‑polling [+]
6. Инструменты тестирования и эмуляции
7. Метрики, трассировка, логгирование out‑of‑the‑box
8. Плагинная архитектура / события
9. Security‑bundle
10. Расширенный форматтер сообщений
11. Версионирование и миграции Telegram‑API
12. Built‑in data‑validation (+ adaptors)
13. Inline‑Query & Web‑App helpers
14. CLI‑утилита / Admin‑бот
15. Auto‑documentation (OpenAPI‑style)
16. Улучшенная работа с файлами и медиапотоками
17. Поддерживаются базы данных: H2, PostgreSQL, MySQL, Oracle (без JDBC-драйвера)
18. Токен бота хранится в зашифрованном виде (ключ для шифрования передаётся в `TokenCipher`) [+]
19. Поддержка локализации ответов команд бота

## Пример использования

```java
public class EchoCommands {

    @BotHandler(type = BotRequestType.MESSAGE,
            converter = BotHandlerConverter.Identity.class)
    @AlwaysMatch
    public BotResponse echo(BotRequest<Message> request) {
        var msg = request.data();
        var send = new SendMessage(msg.getChatId().toString(), msg.getText());
        return BotResponse.builder().method(send).build();
    }
}

Bot bot = BotFactory.INSTANCE.from(token, new BotConfig(), new BotAdapterImpl(bot, converter, provider), "com.example.bot");
bot.start();
```

Внутри обработчиков команд можно использовать `request.localizer().get("key")` для получения локализованного текста.

### Пример использования `StateStore`
```java
BotConfig config = new BotConfig();
config.setStore(new InMemoryStateStore());

Bot bot = BotFactory.INSTANCE.from(token, config, adapter);

// в хендлере
public void handle(BotRequest<Message> req) {
    String state = req.botInfo().store().get(req.user().chatId());
    // ...
    req.botInfo().store().set(req.user().chatId(), "new-state");
}
```
