package io.lonmstalker.tgkit.core.dsl;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

/** Проверка раскладки клавиатуры. */
public class KeyboardLayoutTest {
    private final ObjectMapper mapper = new ObjectMapper();

    @Test
    void layoutToJson() throws Exception {
        InlineKeyboardMarkup kb = new KbBuilder()
                .row(Button.cb("A", "a"), Button.cb("B", "b"))
                .col(Button.cb("C", "c"))
                .grid(2, Button.cb("D", "d"), Button.cb("E", "e"), Button.cb("F", "f"))
                .build();
        String json = mapper.writeValueAsString(kb);
        assertThat(json).contains("\"callback_data\":\"a\"");
        assertThat(kb.getKeyboard()).hasSize(5);
        assertThat(kb.getKeyboard().get(3)).hasSize(2);
    }
}
