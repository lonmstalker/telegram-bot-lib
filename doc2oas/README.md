# Модуль doc2oas

Утилита конвертирует документацию Telegram Bot API в спецификацию OpenAPI и генерирует SDK.

## Зачем

Док позволяет синхронизировать типы и методы Telegram с вашим кодом всего одной командой.

## Предварительные требования

- JDK 21. Скачать можно с [Adoptium](https://adoptium.net/temurin/releases/?version=21).
- Maven (или соответствующий Gradle таск).

Скачивание документации требует сетевого доступа.

## Как это работает

1. `DocCli` скачивает или читает HTML документацию.
2. `OpenApiEmitter` строит `openapi.yaml`, проверяя его парсером.
3. `GeneratorCli` запускает [OpenAPI Generator](https://github.com/OpenAPITools/openapi-generator).

## Сборка

```bash
mvn -pl doc2oas package
```

## Запуск

```bash
java -cp doc2oas/target/doc2oas-<version>.jar io.github.tgkit.doc.cli.DocCli --help
# либо
java -jar doc2oas/target/doc2oas-<version>-shaded.jar --help
```

## Пошаговый пример

```bash
java -cp doc2oas/target/doc2oas-<version>.jar io.github.tgkit.doc.cli.DocCli --api --output build/openapi/telegram.yaml
java -cp doc2oas/target/doc2oas-<version>.jar io.github.tgkit.doc.generator.GeneratorCli \
  --spec build/openapi/telegram.yaml \
  --target build/sdk \
  --language java
```
