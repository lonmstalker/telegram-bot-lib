/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.crypto;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import org.checkerframework.checker.nullness.qual.NonNull;

public class TokenCipherImpl implements TokenCipher {

  private static final String ALGORITHM = "AES";
  private static final String TRANSFORMATION = "AES/GCM/NoPadding";
  private static final int IV_LENGTH = 12;
  private static final int TAG_LENGTH = 128;

  private final SecretKeySpec key;

  public TokenCipherImpl(byte[] keyBytes) {
    byte[] sized = Arrays.copyOf(keyBytes, 16);
    this.key = new SecretKeySpec(sized, ALGORITHM);
  }

  public TokenCipherImpl(@NonNull String key) {
    this(key.getBytes(StandardCharsets.UTF_8));
  }

  @Override
  public @NonNull String encrypt(@NonNull String token) {
    try {
      byte[] iv = new byte[IV_LENGTH];
      new SecureRandom().nextBytes(iv);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.ENCRYPT_MODE, this.key, new GCMParameterSpec(TAG_LENGTH, iv));

      byte[] encrypted = cipher.doFinal(token.getBytes(StandardCharsets.UTF_8));
      byte[] result = new byte[IV_LENGTH + encrypted.length];

      System.arraycopy(iv, 0, result, 0, IV_LENGTH);
      System.arraycopy(encrypted, 0, result, IV_LENGTH, encrypted.length);

      return Base64.getEncoder().encodeToString(result);
    } catch (Exception ex) {
      throw new BotApiException("Unable to encrypt token", ex);
    }
  }

  @Override
  public @NonNull String decrypt(@NonNull String cipherText) {
    try {
      byte[] decoded = Base64.getDecoder().decode(cipherText);
      byte[] iv = Arrays.copyOfRange(decoded, 0, IV_LENGTH);
      byte[] data = Arrays.copyOfRange(decoded, IV_LENGTH, decoded.length);

      Cipher cipher = Cipher.getInstance(TRANSFORMATION);
      cipher.init(Cipher.DECRYPT_MODE, this.key, new GCMParameterSpec(TAG_LENGTH, iv));

      byte[] decrypted = cipher.doFinal(data);
      return new String(decrypted, StandardCharsets.UTF_8);
    } catch (Exception ex) {
      throw new BotApiException("Unable to decrypt token", ex);
    }
  }
}
