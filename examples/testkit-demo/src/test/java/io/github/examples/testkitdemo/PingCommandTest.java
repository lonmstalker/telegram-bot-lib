package io.github.examples.testkitdemo;

import io.github.tgkit.testkit.TelegramBotTest;
import io.github.tgkit.testkit.UpdateInjector;
import io.github.tgkit.testkit.Expectation;
import io.github.tgkit.testkit.TestBotBootstrap;
import io.github.tgkit.flag.test.FlagOverrideExtension;
import io.github.tgkit.flag.test.Flags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(FlagOverrideExtension.class)
@TelegramBotTest
class PingCommandTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void pingPong(UpdateInjector inject, Expectation expect, Flags flags) {
    flags.enable("PING_CMD");
    inject.text("/ping").from(42L).dispatch();
    expect.api("sendMessage").jsonPath("$.text", "pong");
  }
}
