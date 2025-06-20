package io.lonmstalker.tgkit.security.config;

import io.lonmstalker.tgkit.security.antispam.DuplicateProvider;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import io.lonmstalker.tgkit.security.captcha.CaptchaProvider;
import io.lonmstalker.tgkit.security.ratelimit.RateLimiter;
import io.lonmstalker.tgkit.security.secret.SecretStore;
import java.util.Set;
import java.util.concurrent.atomic.AtomicReference;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Глобальная конфигурация модуля безопасности.
 * <p>Позволяет подменять реализации во время работы или тестов.</p>
 *
 * <pre>{@code
 * BotSecurityGlobalConfig.INSTANCE.secrets()
 *     .token("vault-token")
 *     .address("https://vault:8200");
 * }</pre>
 */
public final class BotSecurityGlobalConfig {
  public static final BotSecurityGlobalConfig INSTANCE = new BotSecurityGlobalConfig();

  private final @NonNull SecretsGlobalConfig secrets = new SecretsGlobalConfig();
  private final @NonNull AuditGlobalConfig audit = new AuditGlobalConfig();
  private final @NonNull RateLimitGlobalConfig rateLimit = new RateLimitGlobalConfig();
  private final @NonNull AntiSpamGlobalConfig antiSpam = new AntiSpamGlobalConfig();
  private final @NonNull CaptchaGlobalConfig captcha = new CaptchaGlobalConfig();

  private BotSecurityGlobalConfig() {}

  public @NonNull SecretsGlobalConfig secrets() {
    return secrets;
  }

  public @NonNull AuditGlobalConfig audit() {
    return audit;
  }

  public @NonNull RateLimitGlobalConfig rateLimit() {
    return rateLimit;
  }

  public @NonNull AntiSpamGlobalConfig antiSpam() {
    return antiSpam;
  }

  public @NonNull CaptchaGlobalConfig captcha() {
    return captcha;
  }

  /** Конфигурация хранилища секретов. */
  public static final class SecretsGlobalConfig {
    private final AtomicReference<SecretStore> store = new AtomicReference<>();
    private final AtomicReference<String> token = new AtomicReference<>();
    private final AtomicReference<String> address = new AtomicReference<>();

    public SecretStore getStore() {
      return store.get();
    }

    public @NonNull SecretsGlobalConfig store(@NonNull SecretStore store) {
      this.store.set(store);
      return this;
    }

    public String token() {
      return token.get();
    }

    public @NonNull SecretsGlobalConfig token(String token) {
      this.token.set(token);
      return this;
    }

    public String address() {
      return address.get();
    }

    public @NonNull SecretsGlobalConfig address(String address) {
      this.address.set(address);
      return this;
    }
  }

  /** Конфигурация подсистемы аудита. */
  public static final class AuditGlobalConfig {
    private final AtomicReference<AuditBus> bus = new AtomicReference<>();

    public AuditBus bus() {
      return bus.get();
    }

    public @NonNull AuditGlobalConfig bus(@NonNull AuditBus bus) {
      this.bus.set(bus);
      return this;
    }
  }

  /** Конфигурация backend механизма ограничений. */
  public static final class RateLimitGlobalConfig {
    private final AtomicReference<RateLimiter> backend = new AtomicReference<>();

    public RateLimiter getBackend() {
      return backend.get();
    }

    public @NonNull RateLimitGlobalConfig backend(@NonNull RateLimiter backend) {
      this.backend.set(backend);
      return this;
    }
  }

  /** Настройки антиспама. */
  public static final class AntiSpamGlobalConfig {
    private final AtomicReference<DuplicateProvider> duplicateProvider =
        new AtomicReference<>();
    private final AtomicReference<Set<String>> blacklist = new AtomicReference<>(Set.of());

    public DuplicateProvider duplicateProvider() {
      return duplicateProvider.get();
    }

    public @NonNull AntiSpamGlobalConfig duplicateProvider(@NonNull DuplicateProvider provider) {
      this.duplicateProvider.set(provider);
      return this;
    }

    public Set<String> blacklistDomains() {
      return blacklist.get();
    }

    public @NonNull AntiSpamGlobalConfig blacklistDomains(@NonNull Set<String> domains) {
      this.blacklist.set(domains);
      return this;
    }
  }

  /** Конфигурация провайдера капчи. */
  public static final class CaptchaGlobalConfig {
    private final AtomicReference<CaptchaProvider> provider = new AtomicReference<>();

    public CaptchaProvider provider() {
      return provider.get();
    }

    public @NonNull CaptchaGlobalConfig provider(@NonNull CaptchaProvider provider) {
      this.provider.set(provider);
      return this;
    }
  }
}
