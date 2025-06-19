# Валидаторы TG-Kit – быстрый способ защитить бота от мусора и злоупотреблений

TG-Kit уже включает десятки готовых проверок.  
Используйте их напрямую или комбинируйте в собственные цепочки.

| Категория | Класс-фасад | Шорткаты | Что проверяет |
|-----------|-------------|----------|---------------|
| **Текст** | `TextValidators` | `notBlank()`, `maxLength(n)`<br>`noProfanity()` | Пустота, длина, токсичность |
| **URL / URI** | `UrlValidators` | `validUri()`, `httpsOnly()`, `safeBrowsing()` | Формат, схема, Google Safe Browsing |
| **Фото** | `PhotoValidators` | `minResolution(w,h)`, `maxSizeKb(kb)`, `safeSearch()` | Разрешение, объём, NSFW |
| **Видео / VideoNote** | `VideoValidators` | `maxSizeMb(mb)`, `maxDurationSec(sec)`, `safeSearch()` | Объём, длительность, NSFW |
| **Документы** | `DocumentValidators` | `allowedMime(Set)`, `maxSizeMb()` | MIME-type, размер |
| **Гео-данные** | `LocationValidators` | `inBounds(box)` | Координаты внутри полигона |
| **Контакты** | `ContactValidators` | `validPhone()`, `validName()` | Формат номера, имя |
| **Платежи** | `PaymentValidators` | `validAmount(min,max)`, `validCurrency(Set)` | Сумма и валюта счёта |
| **Расширенные** | `AdvancedValidators` | `noForeignLinks(domains)`, `payerMatchesAuthor()`<br>`futureDate(maxDays)` | Ссылки, сопоставление плательщика, даты |
| **Медиа-прочее** | `MiscValidators` | `maxVoiceDuration(sec)`, `allowStickerSet(set)` | Голосовые, пакеты стикеров |

## Мини-пример

```java
wizard.step("photo")
      .expectPhoto()                               // Message→List<PhotoSize>
      .validate(PhotoValidators.minResolution(256, 256))
      .validate(PhotoValidators.safeSearch())      // G-Vision / Cloud Moderation
      .save(UserProfile::setAvatar)
      .build();
```

---

## Как написать свой валидатор

```java
public final class AgeValidators {
    public static Validator<Integer> between(int min, int max) {
        return Validator.of(
            age -> age >= min && age <= max,
            MessageKey.of("error.age.outOfRange", min, max));
    }
}
```

И сразу применяем:
```java
.expectInt()
.validate(AgeValidators.between(18, 99))
```

---

## FAQ

| Вопрос | Ответ                                                                                                                                                                                                                           |
|--------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|
| **Зачем нужны валидаторы, если можно проверять «вручную»?** | Валидатор — это единый объект с методом `void validate(T value)`, который бросает `ValidationException`. Такой подход позволяет переиспользовать проверку в разных сценариях и писать к ней юнит-тесты отдельно от бизнес-кода. |
| **Как объединить несколько проверок?** | На шаге просто вызывайте `.validate(...)` несколько раз — проверки выполняются последовательно, и первая же ошибка бросает `ValidationException`.                                                                               |
| **Как локализуется сообщение об ошибке?** | В `ValidationException` передаётся `MessageKey`. При обработке исключения фреймворк вызывает `i18nService.localize(key, user.lang())` и отправляет пользователю уже переведённый текст.                                         |
| **Как написать собственный валидатор?** | Используйте фабрику `Validator.of(predicate, MessageKey)`. <br>`Validator<Integer> positive = Validator.of(x -> x > 0, MessageKey.of("error.number.positive"));`                                                                |
| **Можно задать параметры проверке (например, лимит размера)?** | У большинства готовых фасадов (например, `VideoValidators`) есть методы вида `maxSizeKb(int kb)`. В своём валидаторе храните параметры как поля обычного POJO.                                                                  |
| **Как протестировать валидатор?** | В юнит-тесте вызовите `validator.validate(value)`. Ожидаемое поведение проверяется через `assertDoesNotThrow` или `assertThrows(ValidationException.class, …)`.                                                                 |
| **Что попадает в логи при ошибке?** | Исключение логируется стандартным логгером TG-Kit: класс валидатора + ключ ошибки. Дополнительные детали (например, «size=12 MB, limit=10 MB») добавляйте в текст `MessageKey` либо в сообщение исключения.                     |
| **Могу ли я валидировать весь `BotRequest`, а не отдельное поле?** | Да. Любой `Validator<BotRequest<?>>` можно добавить к шагу до парсинга, чтобы, например, запретить сообщения без медиа.                                                                                                         |
| **Что произойдёт, если валидатор бросит исключение?** | Движок прекратит обработку шага, отправит пользователю локализованное сообщение об ошибке и снова попросит ввести данные.                                                                                                       |