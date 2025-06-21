package io.github.tgkit.json.dsljson;

import io.github.tgkit.core.bot.BotConfig;
import io.github.tgkit.json.JsonCodec;
import java.util.Locale;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;

/**
 * Бенчмарк сериализации/десериализации.
 */
@State(Scope.Thread)
public class DslJsonCodecBenchmark {
  private final JsonCodec codec = new DslJsonCodec();
  private BotConfig cfg;
  private byte[] data;

  @Setup
  public void setup() throws Exception {
    cfg = BotConfig.builder().botGroup("b").requestsPerSecond(10).locale(Locale.US).build();
    data = codec.toBytes(cfg);
  }

  @Benchmark
  public byte[] write() throws Exception {
    return codec.toBytes(cfg);
  }

  @Benchmark
  public BotConfig read() throws Exception {
    return codec.fromBytes(data, BotConfig.class);
  }
}
