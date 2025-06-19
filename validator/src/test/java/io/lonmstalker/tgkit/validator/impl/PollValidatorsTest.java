package io.lonmstalker.tgkit.validator.impl;

import io.lonmstalker.tgkit.core.exception.ValidationException;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.polls.Poll;
import org.telegram.telegrambots.meta.api.objects.polls.PollOption;

import java.util.List;

import static io.lonmstalker.tgkit.validator.impl.PollValidators.*;
import static org.junit.jupiter.api.Assertions.*;

class PollValidatorsTest {

    private PollOption opt(String text) {
        PollOption o = new PollOption();
        o.setText(text);
        return o;
    }

    private Poll poll(List<PollOption> opts) {
        Poll p = new Poll();
        p.setOptions(opts);
        return p;
    }

    @Test
    void optionsCount_acceptsValid() {
        assertDoesNotThrow(() -> optionsCount().validate(poll(List.of(opt("a"), opt("b")))));
    }

    @Test
    void optionsCount_rejectsTooFew() {
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> optionsCount().validate(poll(List.of(opt("a"))))
        );
        assertEquals("error.poll.count", ex.getErrorKey().key());
    }

    @Test
    void optionTextLength_acceptsValid() {
        PollOption o = opt("x".repeat(100));
        assertDoesNotThrow(() -> optionTextLength().validate(poll(List.of(o, o))));
    }

    @Test
    void optionTextLength_rejectsTooLong() {
        PollOption o = opt("x".repeat(101));
        ValidationException ex = assertThrows(
                ValidationException.class,
                () -> optionTextLength().validate(poll(List.of(o, o)))
        );
        assertEquals("error.poll.optionLength", ex.getErrorKey().key());
    }
}
