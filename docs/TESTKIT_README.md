# tgkit-testkit

## JUnit-расширение для изолированного тестирования Telegram-ботов

Позволяет эмулировать входящие `Update` и проверять обращения к API без сети.

## 📦 Подключение

<details>
<summary>Maven</summary>

```xml
<dependencyManagement>
    <dependencies>
        <dependency>
            <groupId>io.github.tgkit</groupId>
            <artifactId>tgkit-bom</artifactId>
            <version>0.0.1-SNAPSHOT</version>
            <type>pom</type>
            <scope>import</scope>
        </dependency>
    </dependencies>
</dependencyManagement>

<dependency>
    <groupId>io.github.tgkit</groupId>
    <artifactId>tgkit-testkit-core</artifactId>
    <scope>test</scope>
</dependency>
```
</details>
<details>
<summary>Gradle Kotlin DSL</summary>

```kotlin
implementation(platform("io.github.tgkit:tgkit-bom:0.0.1-SNAPSHOT"))
testImplementation("io.github.tgkit:tgkit-testkit-core")
```
</details>

## ✨ Быстрый старт

```java
@TelegramBotTest
class EchoBotTest {

    @Test
    void echo(UpdateInjector inject, Expectation expect) {
        inject.text("/echo hello").from(1L).dispatch();
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

## Фич-флаги в тестах

Модуль `flag-test` содержит расширение `FlagOverrideExtension`.
Оно позволяет включать или выключать флаги прямо в тестовом методе.

```java
@ExtendWith(FlagOverrideExtension.class)
class AbTest {

    @Test
    void variant(UpdateInjector inject, Expectation expect, Flags flags) {
        flags.enable("NEW_FLOW");
        inject.text("/start").from(1L).dispatch();
        expect.api("sendMessage").jsonPath("$.text", "new");
    }
}
```
