package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class KbBuilderTest {

    @Test
    void gridArrangesButtons() {
        MessageLocalizer loc = mock(MessageLocalizer.class);
        KbBuilder kb = new KbBuilder(loc)
                .grid(2,
                        Button.btn("A"), Button.btn("B"),
                        Button.btn("C"));

        InlineKeyboardMarkup mk = kb.build();

        // ожидание: первая строка 2 кнопки, вторая — 1
        assertThat(mk.getKeyboard()).hasSize(2);
        assertThat(mk.getKeyboard().get(0)).hasSize(2);
        assertThat(mk.getKeyboard().get(1)).hasSize(1);
    }
}
