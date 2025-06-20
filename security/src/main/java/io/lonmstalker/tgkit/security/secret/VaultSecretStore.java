package io.lonmstalker.tgkit.security.secret;

import io.github.jopenlibs.vault.Vault;
import io.github.jopenlibs.vault.VaultConfig;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class VaultSecretStore implements SecretStore {

  private static final Logger log = LoggerFactory.getLogger(VaultSecretStore.class);

  private transient Vault vault;

  public VaultSecretStore() {
    this(
        new VaultConfig()
            .token(System.getenv("VAULT_TOKEN"))
            .address(System.getenv().getOrDefault("VAULT_ADDR", "http://localhost:8200")));
  }

  public VaultSecretStore(@NonNull VaultConfig config) {
    if (config.getToken() == null)
      throw new IllegalStateException("VAULT_TOKEN env missing for VaultSecretStore");

    try {
      vault = Vault.create(config);
      log.info("VaultSecretStore connected to {}", config.getAddress());
    } catch (Exception e) {
      throw new RuntimeException("Cannot init Vault client", e);
    }
  }

  @Override
  public Optional<String> get(@NonNull String key) {
    try {
      var resp = vault.logical().read("secret/data/" + key); // KV v2 path
      return Optional.ofNullable(resp.getData().get("data")).map(Object::toString);
    } catch (Exception e) {
      log.warn("Vault read failed for {}", key, e);
      return Optional.empty();
    }
  }
}
