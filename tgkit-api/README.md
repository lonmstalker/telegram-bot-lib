# Модуль tgkit-api

В модуле хранятся DSL и сгенерированные модели Telegram Bot API.

## Генерация моделей

1. Собрать утилиту `doc2oas`:
   ```bash
   mvn -pl doc2oas package
   ```
2. Сгенерировать спецификацию Bot API:
   ```bash
   java -cp doc2oas/target/doc2oas-0.0.1-SNAPSHOT.jar \
     io.github.tgkit.doc.cli.DocCli --input doc2oas/src/test/resources/sample.html \
     --output build/openapi/telegram.yaml
   ```
3. Построить Java модели:
   ```bash
   java -cp doc2oas/target/doc2oas-0.0.1-SNAPSHOT.jar \
     io.github.tgkit.doc.generator.GeneratorCli \
     --spec build/openapi/telegram.yaml \
     --target build/sdk \
     --language java
   ```
4. Скопировать классы в `tgkit-api/src/generated/java` и убедиться,
   что `pom.xml` подключает этот каталог через `build-helper-maven-plugin`.

После генерации модуль компилируется обычной командой `mvn verify`.
