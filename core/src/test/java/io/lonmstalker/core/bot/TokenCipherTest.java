package io.lonmstalker.core.bot;

import io.lonmstalker.core.utils.TokenCipher;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenCipher positive")
class TokenCipherTest {
    @Test
    @DisplayName("шифрование и расшифровка строки")
    void encryptAndDecrypt() {
        TokenCipher cipher = new TokenCipherImpl("1234567890123456");
        String token = "secret";
        String encrypted = cipher.encrypt(token);
        assertNotEquals(token, encrypted);
        assertEquals(token, cipher.decrypt(encrypted));
    }

    @Test
    @DisplayName("шифрование/расшифровка через byte[] ключ")
    void encryptWithBytesKey() {
        TokenCipher cipher = new TokenCipherImpl("1234567890123456".getBytes());
        String token = "data";
        String encrypted = cipher.encrypt(token);
        assertEquals(token, cipher.decrypt(encrypted));
    }

    @Test
    @DisplayName("два вызова encrypt дают разные строки")
    void encryptProducesDifferentValues() {
        TokenCipher cipher = new TokenCipherImpl("1234567890123456");
        String first = cipher.encrypt("tok");
        String second = cipher.encrypt("tok");
        assertNotEquals(first, second);
    }
}
