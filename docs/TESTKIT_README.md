# tgkit-testkit

## JUnit-расширение для изолированного тестирования Telegram-ботов

Позволяет эмулировать входящие `Update` и проверять обращения к API без сети.

## \ud83d\udce6 Подключение

<details>
<summary>Maven</summary>

```xml
<dependency>
    <groupId>io.lonmstalker.tgkit</groupId>
    <artifactId>testkit</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```
</details>
<details>
<summary>Gradle Kotlin DSL</summary>

```kotlin
testImplementation("io.lonmstalker.tgkit:testkit:0.0.1-SNAPSHOT")
```
</details>

## \u2728 Быстрый старт

```java
@TelegramBotTest
class EchoBotTest {

    @Test
    void echo(UpdateInjector inject, Expectation expect) {
        inject.text("/echo hello").from(1L);
        expect.api("sendMessage").jsonPath("$.text", "hello");
    }
}
```

## BotBuilder

```java
Bot bot = BotBuilder.builder()
        .token("TOKEN")
        .addHandler(new EchoCommands())
        .build();
```
