package io.lonmstalker.examples.testkitdemo;

import io.lonmstalker.tgkit.testkit.TelegramBotTest;
import io.lonmstalker.tgkit.testkit.UpdateInjector;
import io.lonmstalker.tgkit.testkit.Expectation;
import org.junit.jupiter.api.Test;

@TelegramBotTest
class PingCommandTest {

    @Test
    void pingPong(UpdateInjector inject, Expectation expect) {
        inject.text("/ping").from(42L);
        expect.api("sendMessage").jsonPath("$.text", "pong");
    }
}
