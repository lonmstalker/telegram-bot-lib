package io.lonmstalker.tgkit.security.init;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.security.antispam.InMemoryDuplicateProvider;
import io.lonmstalker.tgkit.security.audit.AsyncAuditBus;
import io.lonmstalker.tgkit.security.captcha.MathCaptchaOperations;
import io.lonmstalker.tgkit.security.captcha.provider.MathCaptchaProvider;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import io.lonmstalker.tgkit.security.ratelimit.impl.InMemoryRateLimiter;
import io.lonmstalker.tgkit.security.secret.EnvSecretStore;
import java.time.Duration;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang3.Range;

/**
 * Базовая инициализация security-модуля.<br>
 * Аналогично {@link io.lonmstalker.tgkit.core.init.BotCoreInitializer}.
 */
public final class BotSecurityInitializer {

  private static final Logger log = LoggerFactory.getLogger(BotSecurityInitializer.class);

  private BotSecurityInitializer() {}

  private static volatile boolean started;

  public synchronized void init() {
    if (started) {
      log.warn(
          "[sec-init] BotSecurityInitializer уже вызывался, повторная инициализация игнорируется");
      return;
    }
    log.info("[sec-init] Старт инициализации security-подсистемы…");

    // ── CAPTCHA ───────────────────────────────────────────────────────────
    BotSecurityGlobalConfig.INSTANCE
        .captcha()
        .provider(
            MathCaptchaProvider.builder()
                .ttl(Duration.ofMinutes(5))
                .numberRange(Range.of(1, 10))
                .wrongCount(2)
                .allowedOps(MathCaptchaOperations.OPERATIONS)
                .build());

    // ── Rate-Limiter (in-mem) ─────────────────────────────────────────────
    BotSecurityGlobalConfig.INSTANCE.rateLimit().backend(new InMemoryRateLimiter(2_000));

    // ── Anti-Spam ─────────────────────────────────────────────────────────
    BotSecurityGlobalConfig.INSTANCE
        .antiSpam()
        .duplicateProvider(new InMemoryDuplicateProvider(Duration.ofMinutes(1), 1_000))
        .blacklistDomains(Set.of());

    // ── Secrets-Store (ENV vars) ──────────────────────────────────────────
    BotSecurityGlobalConfig.INSTANCE.secrets().store(new EnvSecretStore());

    // ── Audit ──────────────────────────────────────────
    BotSecurityGlobalConfig.INSTANCE
        .audit()
        .bus(new AsyncAuditBus(BotGlobalConfig.INSTANCE.executors().getIoExecutorService(), 100));

    log.info("[sec-init] Security-подсистема успешно инициализирована ✅");
    started = true;
  }
}
