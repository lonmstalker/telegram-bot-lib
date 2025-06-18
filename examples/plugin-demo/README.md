# Проект tgkit-maven-example

Пример много‐модульного Maven-проекта с механизмом плагинов для Telegram-бота.

## Структура проекта

- **core** – библиотека `BotPluginManager` и вспомогательные классы.
- **plugin-example** – пример плагина с собственным `plugin.yml`.
- **app** – приложение, которое сканирует и запускает все плагины из папки `plugin-example/target`.

## Требования

- Java 21+
- Maven 3.8+

## Запуск приложения

1. Из корня проекта выполните:
   ```shell 
   cd ../..
   pwd
   mvn clean package -DskipTests -DskipCheckerFramework=true
   ```
2. Убедитесь, что JAR плагина лежит в plugin-example/target:
   ```shell
   ls plugin-example/target/plugin-example.jar
   ```
3. Запустите модуль app, который автоматически загрузит и выполнит все плагины:
   ```shell
   java -jar app/target/app.jar plugin-example/target
   ```
4. В консоли вы увидите примерно:
   ```
   [Example] onLoad
   [Example] start
   [Example] beforeStop
   [Example] stop
   [Example] afterStop
   [Example] onUnload
   ```