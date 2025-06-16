# tgkit-dsl

## Удобная fluent-обёртка над **Telegram Bot API**.  
Позволяет писать:

```java
BotDSL.msg(ctx, "Привет, мир!").disableNotif().send();
```
вместо громоздкого SendMessage.

## 📦 Установка
```xml
<dependency>
  <groupId>io.lonmstalker</groupId>
  <artifactId>tgkit-dsl</artifactId>
  <version>0.0.1-SNAPSHOT</version>
</dependency>
```

## 🚀 Быстрый старт
```java
// 1. Конфиг один раз при запуске
BotDSL.config(cfg -> cfg
        .markdownV2()          // глобальный parseMode
        .sanitizeMarkdown()    // экранировать спецсимволы
        .missingIdStrategy(MissingIdStrategy.WARN)
);

// 2. В хэндлере Update
DSLContext ctx = BotDSL.ctx(botInfo, userInfo);

BotDSL.msg(ctx, "*Привет!*")
      .disableNotif()
      .keyboard(kb -> kb.row(
              Button.cb("PING", "pong"),
              Button.url("GitHub", "https://github.com")
      ))
      .send();
```

### 🛠️ Доступные билдеры
| **Билдер**            | **Фабрика**                        | **Назначение**                       |
|-----------------------|------------------------------------|--------------------------------------|
| `ChatMessageBuilder`  | `BotDSL.msg(ChatCtx, text)`        | Текстовое сообщение в чат            |
| `InlineMessageBuilder`| `BotDSL.msg(InlineCtx, text)`      | Сообщение для inline-ответа          |
| `PhotoBuilder`        | `photo(ctx, file)`                 | Фото с подписью                      |
| `MediaGroupBuilder`   | `mediaGroup(ctx)`                  | Альбом (≤ 10 медиа)                  |
| `EditBuilder`         | `edit(ctx, msgId)`                 | Редактирование текста                |
| `DeleteBuilder`       | `delete(ctx, msgId)`               | Удаление сообщения                   |
| `PollBuilder`         | `poll(ctx, question)`              | Обычный опрос                        |
| `QuizBuilder`         | `quiz(ctx, question, correctIdx)`  | Викторина                            |
| `InlineResults`       | `inline(ctx)`                      | Результаты inline-запроса            |

Все билдеры наследуют интерфейс **`Common<T>`** с методами:

* `replyTo()`, `disableNotif()`, `keyboard()`
* `ttl(Duration)` — авто-удаление (если подключён планировщик)
* `flag()`, `flagUser()`, `abTest()` — фича-флаги и A/B-тесты
* `when()`, `onlyAdmin()` — условные ветви

## ✨ Фича-флаги и A/B-тесты
```java
InMemoryFeatureFlags ff = new InMemoryFeatureFlags();
ff.enableChat("NEW_MENU", 42L);   // включили фичу для чата 42
ff.rollout("BETA_TEXT", 30);      // 30 % чатов получают вариант

DslGlobalConfig.INSTANCE.featureFlags(ff);

BotDSL.msg(ctx, "Hello!")
      .flag("NEW_MENU",  ctx, b -> b.keyboard(k -> k.row(Button.btn("Menu"))))
      .abTest("BETA_TEXT", ctx,
              control -> control.parseMode(ParseMode.HTML),
              variant -> variant.parseMode(ParseMode.MARKDOWN_V2))
      .send();
Метод	    Кого проверяет	Поведение
flag()	    chatId	        Выполняет ветку, если флаг включён для чата
flagUser()	userId	        Аналогично, но по пользователю
abTest()	chat/user	    CONTROL / VARIANT по rollout-проценту
```

## ✅ Валидация
| **Валидатор**              | **Ограничение Telegram**                       |
|----------------------------|-----------------------------------------------|
| `TextLengthValidator`      | `text` ≤ 4096                                 |
| `CaptionValidator`         | `caption` ≤ 1024                              |
| `MediaGroupSizeValidator`  | медиа-группа ≤ 10 элементов                   |
| `PollValidator`            | `question` ≤ 300, `options` ≤ 10              |
| `FileSizeValidator`        | фото ≤ 20 МБ, другие файлы ≤ 50 МБ            |

> Валидация вызывается **автоматически** внутри `build()` / `send()` соответствующего билдера.

---

## 🔌 Расширяемость
* **Новый билдер** — наследуйтесь от `CommonBuilder<T>` и переопределите `build()`.
* **Свой `FeatureFlags`** — подключите Redis / LaunchDarkly и передайте  
  `DslGlobalConfig.INSTANCE.featureFlags(custom)`.

## 🧪 Тестирование

```java
TelegramSender sender = mock(TelegramSender.class);
doReturn(null).when(sender).execute(any());

DSLContext ctx = BotDSL.ctx(bot, user);

BotDSL.msg(ctx, "test").send();   // execute() не ходит в сеть
verify(sender).execute(any());
```

## 🙋 FAQ

* **Q:** Как отправить MarkdownV2 без автоэкранирования?  
  **A:** Не вызывайте `sanitizeMarkdown()` в глобальном конфиге и/или задайте  
  `parseMode(ParseMode.MARKDOWN_V2).sanitize(false)` точечно на нужном сообщении.
* **Q:** Какое дефолтное состояние у флага?  
  **A:** Флаг включен для всех, если явно не выключить для чата/пользователя,
  также можно выключить сам флаг `DslGlobalConfig.INSTANCE.getFlags().disable("flag")`.