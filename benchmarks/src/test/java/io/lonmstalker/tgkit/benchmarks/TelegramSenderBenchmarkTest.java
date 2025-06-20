package io.lonmstalker.tgkit.benchmarks;

import org.junit.jupiter.api.Test;

/** Smoke test for {@link TelegramSenderBenchmark}. */
class TelegramSenderBenchmarkTest {

  @Test
  void executeGetMe() throws Exception {
    TelegramSenderBenchmark bench = new TelegramSenderBenchmark();
    bench.setup();
    bench.executeGetMe();
    bench.tearDown();
  }
}
