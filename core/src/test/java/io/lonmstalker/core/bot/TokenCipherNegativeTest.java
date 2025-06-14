package io.lonmstalker.core.bot;

import io.lonmstalker.core.exception.BotApiException;
import io.lonmstalker.core.utils.TokenCipher;
import io.lonmstalker.core.utils.TokenCipherImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("TokenCipher negative")
class TokenCipherNegativeTest {
    @Test
    @DisplayName("расшифровка с неверным ключом")
    void decryptWithWrongKeyShouldFail() {
        TokenCipher first = new TokenCipherImpl("1234567890123456");
        String enc = first.encrypt("token");
        TokenCipher wrong = new TokenCipherImpl("6543210987654321");
        assertThrows(BotApiException.class, () -> wrong.decrypt(enc));
    }

    @Test
    @DisplayName("расшифровка повреждённых данных")
    void decryptCorruptedDataShouldFail() {
        TokenCipher cipher = new TokenCipherImpl("1234567890123456");
        String enc = cipher.encrypt("token") + "xxx";
        assertThrows(BotApiException.class, () -> cipher.decrypt(enc));
    }
}
