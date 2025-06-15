package io.lonmstalker.tgkit.core.crypto;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;
import io.lonmstalker.tgkit.core.exception.BotApiException;

public class TokenCipherImplTest {

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
    void decrypt_invalid_data_throws() {
        var cipher = new TokenCipherImpl("secretkey123456");
        assertThrows(BotApiException.class, () -> cipher.decrypt("boom"));
    }
}
