package io.lonmstalker.tgkit.core.dsl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

/**
 * Конструктор inline-клавиатуры.
 */
public final class KbBuilder {

    private final @NonNull MessageLocalizer loc;
    private final List<List<InlineKeyboardButton>> rows = new ArrayList<>();

    KbBuilder(@NonNull MessageLocalizer loc) {
        this.loc = loc;
    }

    /** Добавляет строку кнопок. */
    public @NonNull KbBuilder row(@NonNull Button... buttons) {
        rows.add(to(buttons));
        return this;
    }

    /** Каждая кнопка в отдельной строке. */
    public @NonNull KbBuilder col(@NonNull Button... buttons) {
        for (Button b : buttons) {
            rows.add(to(b));
        }
        return this;
    }

    /** Размещает кнопки по сетке. */
    public @NonNull KbBuilder grid(int cols, @NonNull Button... buttons) {
        List<InlineKeyboardButton> cur = new ArrayList<>();
        for (Button b : buttons) {
            cur.add(b.build(loc));
            if (cur.size() == cols) {
                rows.add(cur);
                cur = new ArrayList<>();
            }
        }
        if (!cur.isEmpty()) {
            rows.add(cur);
        }
        return this;
    }

    /** Итоговая разметка. */
    public @NonNull InlineKeyboardMarkup build() {
        return new InlineKeyboardMarkup(rows);
    }

    @NonNull List<List<InlineKeyboardButton>> rows() {
        return rows;
    }

    private @NonNull List<InlineKeyboardButton> to(@NonNull Button... buttons) {
        return Arrays.stream(buttons).map(b -> b.build(loc)).collect(Collectors.toList());
    }

    private @NonNull List<InlineKeyboardButton> to(@NonNull Button b) {
        return List.of(b.build(loc));
    }
}
