package io.lonmstalker.tgkit.benchmarks;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.testkit.TelegramMockServer;
import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import java.util.Locale;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.telegram.telegrambots.meta.api.methods.GetMe;

/** Benchmark measuring TelegramSender#execute against TelegramMockServer. */
@State(Scope.Benchmark)
public class TelegramSenderBenchmark {

  private TelegramMockServer server;
  private TelegramSender sender;

  /** Prepares mock server and sender. */
  @Setup
  public void setup() {
    TestBotBootstrap.initOnce();
    server = new TelegramMockServer();
    BotConfig config =
        BotConfig.builder()
            .baseUrl(server.baseUrl())
            .botGroup("bench")
            .requestsPerSecond(30)
            .locale(Locale.US)
            .build();
    sender = new TelegramSender(config, "TOKEN");
    server.enqueue("{\"ok\":true,\"result\":{\"id\":1}}");
  }

  /** Cleans up resources. */
  @TearDown
  public void tearDown() {
    sender.close();
    server.close();
  }

  /** Sends GetMe request via {@link TelegramSender#execute}. */
  @Benchmark
  public void executeGetMe() throws Exception {
    sender.execute(new GetMe());
  }
}
