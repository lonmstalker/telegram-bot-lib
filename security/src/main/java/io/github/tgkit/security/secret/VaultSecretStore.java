/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.github.tgkit.security.secret;

import io.github.jopenlibs.vault.Vault;
import io.github.jopenlibs.vault.VaultConfig;
import io.github.tgkit.security.config.BotSecurityGlobalConfig;
import java.util.Optional;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Хранилище секретов, использующее HashiCorp Vault.
 *
 * <pre>{@code
 * VaultSecretStore store = new VaultSecretStore();
 * Optional<String> token = store.get("bot-token");
 * }</pre>
 */
public final class VaultSecretStore implements SecretStore {

  private static final Logger log = LoggerFactory.getLogger(VaultSecretStore.class);

  private transient Vault vault;

  public VaultSecretStore() {
    this(new VaultConfig());
  }

  public VaultSecretStore(@NonNull VaultConfig config) {
    String token =
        config.getToken() != null
            ? config.getToken()
            : BotSecurityGlobalConfig.INSTANCE.secrets().token();
    if (token == null) {
      token = System.getenv("VAULT_TOKEN");
    }

    String addr =
        config.getAddress() != null
            ? config.getAddress()
            : BotSecurityGlobalConfig.INSTANCE.secrets().address();
    if (addr == null) {
      addr = System.getenv("VAULT_ADDR");
    }
    if (addr == null) {
      addr = "https://localhost:8200";
    }

    if (token == null) {
      throw new IllegalStateException(
          "Vault token missing: configure BotSecurityGlobalConfig.INSTANCE.secrets().token() or VAULT_TOKEN env");
    }

    try {
      vault = Vault.create(config.token(token).address(addr));
      log.info("VaultSecretStore connected to {}", addr);
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
