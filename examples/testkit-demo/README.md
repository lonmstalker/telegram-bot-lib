# TestKit Demo

Минимальный пример использования `@TelegramBotTest` и `Expectation`.

```java
@TelegramBotTest
class PingCommandTest {

  @Test
  void pingPong(UpdateInjector inject, Expectation expect) {
    inject.text("/ping").from(42L);
    expect.api("sendMessage").jsonPath("$.text", "pong");
  }
}
```
