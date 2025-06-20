Цель: единый справочник для всех AI-агентов (Codex) при работе с репозиторием telegram-bot-lib. Файл задаёт правила, роли, стандарты качества и примерные промпты, чтобы изменения были «Google-grade».

## 1. Общие принципы 🔑

| Правило                | Пояснение                                                          |
|------------------------|--------------------------------------------------------------------|
| Google Java Style      | Статический контроль — `spotless-maven-plugin` + `google-java-format 1.17`.|
| SemVer + Revapi-gate   | Ломаем public API — только при `MAJOR++`.                           |
| Null-Safety            | NullAway без предупреждений — сборка падает при выявленных null-рискax. |
| Покрытие ≥ 90 %        | JaCoCo на модуль и монорепо; property-based tests приветствуются.   |
| CI-green first         | Любой PR обязан проходить `mvn spotless:apply verify`.              |
| Док-Driven             | Каждый новый public-класс ⇢ JavaDoc + пример использования.         |

## 2. Структура репозитория 📂

```
telegram-bot-lib/
├─ core/          # Основная библиотека (JPMS module `telegram.bot.core`)
├─ testkit/       # JUnit-заглушка Telegram + вспомогательные DSL
├─ plugin/         # Плагины reference (Weather, Polls…)
├─ examples/       # Полноценные demo-боты
└─ pom.xml                     # Parent (enforcer, Revapi, Spotless, JaCoCo)
```

Новые модули — заводим только через изменение `parent/pom.xml` и описываем их ниже в «Матричном разделе».

## 3. Роли агентов 🤖

| Агент         | Задачи                                                     | Выходные данные                           |
|---------------|------------------------------------------------------------|-------------------------------------------|
| BuilderAgent  | Реализует фичи или баг-фиксы.                             | Дифф-патчи (`git diff`-формат).           |
| TestAgent     | Пишет/обновляет юнит- и интеграционные тесты (TestKit).    | Дифф-патчи + файлы `*.java`.              |
| LinterAgent   | Запускает Spotless / Checkstyle, чинит стиль.             | Дифф-патчи.                               |
| DocAgent      | Обновляет README, CHANGELOG, JavaDoc.                     | Дифф-патчи (Markdown/Java).               |
| ReviewerAgent | Делает code-review: ищет smells, оценка `O(·)`, даёт комментарии, но не меняет код. | Markdown-ответ с замечаниями. |
| ReleaseAgent  | При необходимости повышает версию, обновляет CHANGELOG, генерирует SBOM. | Дифф-патчи + готовый tag-name. |

Один запрос — одна роль. Если задача комплексная, сначала запускаем Builder, потом Test, Linter, Reviewer.

## 4. Обязательный workflow 🚦

1. Issue / Prompt описывает задачу (фича, баг, рефакторинг).
2. BuilderAgent вносит изменения → создаёт Pull Request.
3. TestAgent добавляет/обновляет тесты (если Builder не покрыл ≥ 90 %).
4. LinterAgent гарантирует нулевые нарушения стиля.
5. CI (`mvn spotless:apply verify`) должен «позеленеть».
6. ReviewerAgent оставляет минимум 2 замечания или одобряет.
7. ReleaseAgent при необходимости увеличивает версию и обновляет лог.

## 5. Требования к патчам 🩹

- Дифф-формат — `git diff --staged`, без декоративного текста.
- Ограничьте область изменения: не трогаем несвязанные файлы.
- Без секретов: токены/пароли заменяем `***`.
- No-op build: если меняются только тесты/доки, основной код не должен пересобираться медленнее.

## 6. Пример промптов 📨

### 6.1 BuilderAgent (добавить поддержку `/help`):

```makefile
Контекст: core.
Задача: добавить команду /help, которая отправляет список зарегистрированных команд.
Результат: git-diff патчами, Google Style, тестовое покрытие ≥90 %.
```

### 6.2 TestAgent (покрытие для Wizard):

```makefile
Контекст: testkit.
Задача: написать property-based тест, проверяющий, что Wizard всегда
переходит в FINAL_STEP после 3 шагов, независимо от входа.
Результат: только diff-патчи.
```

### 6.3 LinterAgent (правит стиль):

```makefile
Контекст: build красный из-за Checkstyle.
Задача: исправить нарушения, ничего функционально не менять.
```

### 6.4 ReviewerAgent:

```less
Контекст: PR #42 (diff приложен).
Задача: code-review, минимум 2 замечания (производительность, безопасность).
```

## 7. Матрица модулей (обновлять при добавлении) 📊

| Модуль                | Java-версия | JPMS-имя           | Public API уровень | Покрытие  | Особые плагины |
|-----------------------|-------------|--------------------|--------------------|-----------|----------------|
| core                  | 21          | telegram.bot.core  | public     | ≥ 90 %    | Revapi, NullAway |
| testkit               | 21          | —                  | incubating | ≥ 95 %    | JUnit 5          |
| plugin                | 21          | разные             | incubating | ≥ 80 %    | SPI loader       |
| validator             | 21          | io.lonmstalker.tgkit.validator | public     | ≥ 90 %    | —               |

## 8. Проверка качества ✅

- `mvn spotless:apply verify` должен проходить без WARNING.
- В среде Codex перед запуском Maven экспортируйте переменные прокси:

```bash
export http_proxy=http://proxy:8080
export https_proxy=http://proxy:8080
export MAVEN_OPTS="-Dhttps.proxyHost=proxy -Dhttps.proxyPort=8080 \
-Dhttp.proxyHost=proxy -Dhttp.proxyPort=8080 \
-Djava.net.preferIPv4Stack=true -Dfile.encoding=UTF-8"
```
Без них сборка может завершиться ошибкой «Network is unreachable».
- `mvn -pl :core -q test-compile` не выводит NullAway ошибок.
- `java -jar core/target/*-full.jar --dry-run` — старт ≤ 1 с.
- `revapi:check` — 0 breaking-changes при неизменённом MAJOR.

## 9. Что нельзя ⛔

- Вставлять непроверенный код из интернета без лицензии (Apache 2.0 или MIT только).
- Изменять public API без bump версии + Revapi-исключения.
- Игнорировать падающие тесты — сначала исправляем, потом коммитим.

## 10. FAQ 💬

**Q:** Можно ли использовать Lombok?
**A:** Нет. Мы мигрируем к Immutables / AutoValue; новый код без Lombok.

**Q:** Нужен ли `@Override` у методов интерфейса?
**A:** Да, Checkstyle настроен на обязательность.

**Q:** Как протестировать Webhook?
**A:** Через `TelegramMockServer` из `testkit`

```java
@TelegramBotTest
void pingPong(UpdateInjector inject, Expectation expect) {
    inject.text("/ping").from(42L);
    expect.api("sendMessage").jsonPath("$.text", "pong");
}
```

Помни: твой код должен быть понятен как разработчику Google, который его увидит через 5 лет.
