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
package io.lonmstalker.tgkit.core.crypto;

import io.lonmstalker.tgkit.testkit.TestBotBootstrap;
import static org.junit.jupiter.api.Assertions.*;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.junit.jupiter.api.Test;

public class TokenCipherImplTest {

  static {
    TestBotBootstrap.initOnce();
  }

  @Test
  void encrypt_decrypt_roundtrip() {
    var cipher = new TokenCipherImpl("secretkey123456");
    var original = "myToken";
    var encrypted = cipher.encrypt(original);
    assertNotEquals(original, encrypted, "encrypted text should differ");
    var decrypted = cipher.decrypt(encrypted);
    assertEquals(original, decrypted);
  }

  @Test
  void encrypt_decrypt_roundtrip_256bit_key() {
    var cipher = new TokenCipherImpl("0123456789abcdef0123456789abcdef");
    var original = "myToken";
    var encrypted = cipher.encrypt(original);
    assertNotEquals(original, encrypted, "encrypted text should differ");
    var decrypted = cipher.decrypt(encrypted);
    assertEquals(original, decrypted);
  }

  @Test
  void decrypt_invalid_data_throws() {
    var cipher = new TokenCipherImpl("secretkey123456");
    assertThrows(BotApiException.class, () -> cipher.decrypt("boom"));
  }

  @Test
  void encrypt_returns_different_values_each_time() {
    var cipher = new TokenCipherImpl("secretkey123456");
    var token = "repeat";

    var first = cipher.encrypt(token);
    var second = cipher.encrypt(token);

    assertNotEquals(first, second, "IV must ensure unique ciphertext");
    assertEquals(token, cipher.decrypt(first));
    assertEquals(token, cipher.decrypt(second));
  }
}
