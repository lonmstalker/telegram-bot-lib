# Модуль doc2oas

Утилита преобразует документацию Telegram Bot API в спецификацию OpenAPI и генерирует SDK.

## Зачем
Док позволяет синхронизировать типы и методы Telegram с вашим кодом всего одной командой.

## Как это работает
1. `DocCli` скачивает или читает HTML документацию.
2. `OpenApiEmitter` строит `openapi.yaml`, проверяя его парсером.
3. `GeneratorCli` запускает [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator).

## Пошаговый пример
```bash
java -jar doc2oas.jar --api --output build/openapi/telegram.yaml
java -cp doc2oas.jar io.lonmstalker.tgkit.doc.generator.GeneratorCli \
  --spec build/openapi/telegram.yaml --target build/sdk
```
