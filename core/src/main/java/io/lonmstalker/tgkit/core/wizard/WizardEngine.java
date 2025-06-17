package io.lonmstalker.tgkit.core.wizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.state.StateStore;
import org.apache.commons.lang3.StringUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;

public class WizardEngine {
    private final ObjectMapper mapper = new ObjectMapper();

    public @NonNull BotResponse handle(@NonNull BotRequest<?> req, @NonNull WizardMeta wizard) {
        Parsed in = parse(req);
        if (in == null) {
            return new BotResponse();
        }

        BotService service = req.service();
        StateStore store = service.store();
        String key = in.chatId + ":" + wizard.id();
        WizardSession session = load(store, key);

        if ("/cancel".equals(in.text) || "wiz:cancel".equals(in.text)) {
            store.set(key, "");
            return sendMessage(in.chatId, service.localizer().get("wizard.cancel"));
        }

        if (!processNav(in.text, session)) {
            StepMeta step = wizard.steps().get(session.getStepIdx());
            if (step.validator() == null || step.validator().validate(in.text)) {
                session.getData().put(step.saveKey(), in.text);
                session.setStepIdx(session.getStepIdx() + 1);
            } else {
                return sendMessage(in.chatId, service.localizer().get("wizard.invalid"));
            }
        }

        if (session.getStepIdx() >= wizard.steps().size()) {
            store.set(key, "");
            return sendMessage(in.chatId, service.localizer().get("wizard.done"));
        }

        store.set(key, toJson(session));
        return askStep(session, wizard, in.chatId, service);
    }

    private record Parsed(String chatId, String text) {
    }

    private @Nullable Parsed parse(@NonNull BotRequest<?> req) {
        if (req.data() instanceof Message msg) {
            return new Parsed(msg.getChatId().toString(), msg.getText());
        }
        if (req.data() instanceof CallbackQuery cb) {
            return new Parsed(cb.getMessage().getChatId().toString(), cb.getData());
        }
        return null;
    }

    private @NonNull WizardSession load(@NonNull StateStore store, @NonNull String key) {
        String raw = store.get(key);
        if (raw == null) {
            return new WizardSession();
        }
        try {
            return mapper.readValue(raw, WizardSession.class);
        } catch (JsonProcessingException e) {
            return new WizardSession();
        }
    }

    private boolean processNav(@NonNull String text, @NonNull WizardSession session) {
        if ("/back".equals(text) || "wiz:back".equals(text)) {
            if (session.getStepIdx() > 0) {
                session.setStepIdx(session.getStepIdx() - 1);
            }
            return true;
        }
        if ("/next".equals(text) || "wiz:next".equals(text)) {
            session.setStepIdx(session.getStepIdx() + 1);
            return true;
        }
        return false;
    }

    private BotResponse askStep(@NonNull WizardSession session,
                                @NonNull WizardMeta wizard,
                                @NonNull String chatId,
                                @NonNull BotService service) {
        StepMeta next = wizard.steps().get(session.getStepIdx());
        String ask = next.defaultAsk() != null
                ? service.localizer().get(next.askKey(), next.defaultAsk())
                : service.localizer().get(next.askKey());
        SendMessage msg = new SendMessage(chatId, ask);
        msg.setReplyMarkup(nav());
        return BotResponse.builder().method(msg).build();
    }

    private @NonNull BotResponse sendMessage(@NonNull String chatId,
                                             @NonNull String text) {
        SendMessage msg = new SendMessage(chatId, text);
        return BotResponse.builder().method(msg).build();
    }

    private @NonNull InlineKeyboardMarkup nav() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("◀").callbackData("wiz:back").build());
        row.add(InlineKeyboardButton.builder().text("▶").callbackData("wiz:next").build());
        row.add(InlineKeyboardButton.builder().text("✖").callbackData("wiz:cancel").build());
        return new InlineKeyboardMarkup(List.of(row));
    }

    private @NonNull String toJson(@NonNull WizardSession session) {
        try {
            return mapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            return StringUtils.EMPTY;
        }
    }
}
