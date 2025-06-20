# Модуль telegram-doc-to-openapi

Набор утилит для конвертации документации Telegram Bot API в спецификацию OpenAPI.

## Использование

```bash
mvn -pl telegram-doc-to-openapi/cli -q exec:java
```

По умолчанию результат сохраняется в `build/openapi/telegram.yaml`.
