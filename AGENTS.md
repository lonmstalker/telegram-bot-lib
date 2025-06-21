## 1. Инварианты «Google level»

| Категория        | Требование                                                                                  | Метрика “прошёл/не прошёл”       |
|------------------|---------------------------------------------------------------------------------------------|----------------------------------|
| **Код**          | Соответствует [Google Java Style Guide](https://google.github.io/styleguide/javaguide.html) | `spotless:check` зелёный         |
| **Тесты**        | ≥ 90 % line coverage (core), contract-тесты на Bot API                                      | `jacoco.xml` в Sonar ≥ 0.90      |
| **Prod-Ready**   | Lat p99 ⩽ 200 ms при 1 k RPS LongPolling                                                    | Gatling CI-джоб                  |
| **Безопасность** | 0 high-severity CVE в зависимостях                                                          | OWASP-dependency-check report    |
| **Docs**         | Javadoc для 100 % public API                                                                | `mvn javadoc:javadoc` без ошибок |

## 2. Слои API — контракты и гарантии

| Слой             | Где лежит                                                             | Гарантия                             | Версионирование |
|------------------|-----------------------------------------------------------------------|--------------------------------------|-----------------|
| **Public**       | `io.github.tgkit.api.*`, модуль `tgkit-api`                           | SemVer; ломается только в major      | `1.x.y`         |
| **Internal**     | `io.github.tgkit.internal.*`, модуль `tgkit-core` (не экспортируется) | Можно ломать в minor/patch           | same as core    |
| **Experimental** | `io.github.tgkit.experiment.*`, модуль `tgkit-experiment`             | Ломается всегда; gated feature-flags | `1.x.y-alpha-n` |

*CI-Revapi* блокирует breaking-changes public-части без major-bump’a.

## 3. Модель данных (Data Model Agent)

**Принципы**

1. Иммутабельность — Java 21 `record`.
2. Null-safety — `@NonNull` по умолчанию, `Optional<T>` для nullable.
3. Sealed-иерархии для Update/Message/CallbackQuery.
4. Public api не должно зависеть от internal/experimental
5. JavaDoc на русском языке

## 4. Политика устаревания (Применяется, когда версия библиотеки будет не равна 0.0.1-SNAPSHOT)

| Стадия               | Шаги                             | Минимальный срок |
|----------------------|----------------------------------|------------------|
| **Deprecate**        | `@Deprecated`, Javadoc alt-path  | 1 minor          |
| **Schedule Removal** | `@ApiStatus.ScheduledForRemoval` | 2 minor          |
| **Remove**           | Удалить код + major-bump         | –                |

## 10. Contribution Agent

* CLA — Google-style.

## 12. Заключение

`agents.md` — линия обороны качества: соблюдая эти правила, мы гарантируем Google-grade DX,
предсказуемость и надёжность **tgkit**.
Не забывайте запускать `mvn verify` перед каждым коммитом ― и счастливо хакать! 🚀