package io.lonmstalker.tgkit.security.secret;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.security.TestUtils;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VaultSecretStore – конфигурация через переменные")
class VaultSecretStoreTest implements WithAssertions {

  @AfterEach
  void cleanup() {
    TestUtils.setEnv("VAULT_ADDR", null);
    TestUtils.setEnv("VAULT_TOKEN", null);
    BotSecurityGlobalConfig.INSTANCE.secrets().token(null).address(null);
  }

  @Test
  @DisplayName("адрес по умолчанию https://localhost")
  void defaultAddress() {
    TestUtils.setEnv("VAULT_TOKEN", "t");
    VaultSecretStore store = new VaultSecretStore();
    var vault = TestUtils.extract(store, "vault");
    var cfg = TestUtils.extract(vault, "vaultConfig");
    assertThat(cfg.getAddress()).isEqualTo("https://localhost:8200");
  }

  @Test
  @DisplayName("VAULT_ADDR переопределяет схему")
  void envOverridesScheme() {
    TestUtils.setEnv("VAULT_TOKEN", "t");
    TestUtils.setEnv("VAULT_ADDR", "http://vault:8300");
    VaultSecretStore store = new VaultSecretStore();
    var vault = TestUtils.extract(store, "vault");
    var cfg = TestUtils.extract(vault, "vaultConfig");
    assertThat(cfg.getAddress()).isEqualTo("http://vault:8300");
  }

  @Test
  @DisplayName("конфигурация задаёт токен и адрес")
  void configOverridesEnv() {
    BotSecurityGlobalConfig.INSTANCE.secrets().token("cfg-token").address("http://cfg-vault:8300");

    VaultSecretStore store = new VaultSecretStore();

    var vault = TestUtils.extract(store, "vault");
    var cfg = TestUtils.extract(vault, "vaultConfig");

    assertThat(cfg.getToken()).isEqualTo("cfg-token");
    assertThat(cfg.getAddress()).isEqualTo("http://cfg-vault:8300");
  }

  @Test
  @DisplayName("ошибка при отсутствии токена")
  void missingToken() {
    assertThatThrownBy(VaultSecretStore::new)
        .isInstanceOf(IllegalStateException.class)
        .hasMessageContaining("Vault token missing");
  }
}
