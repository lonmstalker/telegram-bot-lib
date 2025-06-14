package io.lonmstalker.core.bot;

import io.lonmstalker.core.utils.TokenCipher;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class TokenCipherTest {
    @Test
    void encryptAndDecrypt() {
        TokenCipher cipher = new TokenCipherImpl("1234567890123456");
        String token = "secret";
        String encrypted = cipher.encrypt(token);
        assertNotEquals(token, encrypted);
        assertEquals(token, cipher.decrypt(encrypted));
    }
}
