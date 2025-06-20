package io.lonmstalker.tgkit.security.secret;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.security.TestUtils;
import org.assertj.core.api.WithAssertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("VaultSecretStore – env config")
class VaultSecretStoreTest implements WithAssertions {

  @AfterEach
  void cleanup() {
    TestUtils.setEnv("VAULT_ADDR", null);
    TestUtils.setEnv("VAULT_TOKEN", null);
  }

  @Test
  @DisplayName("default https localhost")
  void defaultAddress() {
    TestUtils.setEnv("VAULT_TOKEN", "t");
    VaultSecretStore store = new VaultSecretStore();
    var vault = TestUtils.extract(store, "vault");
    var cfg = TestUtils.extract(vault, "vaultConfig");
    assertThat(cfg.getAddress()).isEqualTo("https://localhost:8200");
  }

  @Test
  @DisplayName("VAULT_ADDR overrides scheme")
  void envOverridesScheme() {
    TestUtils.setEnv("VAULT_TOKEN", "t");
    TestUtils.setEnv("VAULT_ADDR", "http://vault:8300");
    VaultSecretStore store = new VaultSecretStore();
    var vault = TestUtils.extract(store, "vault");
    var cfg = TestUtils.extract(vault, "vaultConfig");
    assertThat(cfg.getAddress()).isEqualTo("http://vault:8300");
  }
}
