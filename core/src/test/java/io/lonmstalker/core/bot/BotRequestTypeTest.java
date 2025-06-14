import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.exception.BotApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("BotRequestType")
class BotRequestTypeTest {
    @Test
    @DisplayName("checkType бросает исключение при неправильном классе")
    void checkTypeShouldThrowForWrongClass() {
        assertThrows(BotApiException.class, () -> BotRequestType.MESSAGE.checkType(String.class));
    }
}
