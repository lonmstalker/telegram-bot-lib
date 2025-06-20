package io.lonmstalker.tgkit.testkit;

import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Utility class for test bootstrapping. Ensures {@link BotCoreInitializer} is
 * initialized only once per JVM.
 */
public final class TestBotBootstrap {

  private static final AtomicBoolean INITIALIZED = new AtomicBoolean();

  private TestBotBootstrap() {}

  /**
   * Initializes tgkit core exactly once.
   *
   * <pre>{@code
   * TestBotBootstrap.initOnce();
   * }</pre>
   */
  public static void initOnce() {
    if (INITIALIZED.compareAndSet(false, true)) {
      BotCoreInitializer.init();
    }
  }
}
