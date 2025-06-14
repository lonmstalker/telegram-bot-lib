package io.lonmstalker.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

public class TokenCipherImplTest {

    @Test
    void encryptDecryptRoundtrip() {
        var cipher = new TokenCipherImpl("secretkey123456");
        var original = "myToken";
        var encrypted = cipher.encrypt(original);
        assertNotEquals(original, encrypted, "encrypted text should differ");
        var decrypted = cipher.decrypt(encrypted);
        assertEquals(original, decrypted);
    }
}
