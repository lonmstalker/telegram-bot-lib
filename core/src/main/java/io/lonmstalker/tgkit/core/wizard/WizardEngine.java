package io.lonmstalker.tgkit.core.wizard;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.state.StateStore;
import org.checkerframework.checker.nullness.qual.NonNull;
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
        BotInfo info = req.botInfo();
        String chatId;
        String text;
        if (req.data() instanceof Message msg) {
            chatId = msg.getChatId().toString();
            text = msg.getText();
        } else if (req.data() instanceof CallbackQuery cb) {
            chatId = cb.getMessage().getChatId().toString();
            text = cb.getData();
        } else {
            return BotResponse.empty();
        }
        StateStore store = info.store();
        String key = chatId + ":" + wizard.id();
        WizardSession session;
        String raw = store.get(key);
        if (raw == null) {
            session = new WizardSession();
        } else {
            try {
                session = mapper.readValue(raw, WizardSession.class);
            } catch (JsonProcessingException e) {
                session = new WizardSession();
            }
        }

        // text уже получен из update выше
        if ("/cancel".equals(text)) {
            store.set(key, "");
            return sendMessage(chatId, info.localizer().get("wizard.cancel", "Отменено"));
        }
        if ("/back".equals(text)) {
            if (session.getStepIdx() > 0) {
                session.setStepIdx(session.getStepIdx() - 1);
            }
        } else if (!"/next".equals(text)) {
            StepMeta step = wizard.steps().get(session.getStepIdx());
            if (step.validator() == null || step.validator().validate(text)) {
                session.getData().put(step.saveKey(), text);
                session.setStepIdx(session.getStepIdx() + 1);
            } else {
                return sendMessage(chatId, info.localizer().get("wizard.invalid", "Неверный ввод"));
            }
        } else {
            session.setStepIdx(session.getStepIdx() + 1);
        }

        if (session.getStepIdx() >= wizard.steps().size()) {
            store.set(key, "");
            return sendMessage(chatId, info.localizer().get("wizard.done", "Готово"));
        }

        store.set(key, toJson(session));
        StepMeta next = wizard.steps().get(session.getStepIdx());
        String ask = info.localizer().get(next.askKey(), next.defaultAsk());
        SendMessage msg = new SendMessage(chatId, ask);
        msg.setReplyMarkup(nav());
        return BotResponse.builder().method(msg).build();
    }

    private BotResponse sendMessage(String chatId, String text) {
        SendMessage msg = new SendMessage(chatId, text);
        return BotResponse.builder().method(msg).build();
    }

    private InlineKeyboardMarkup nav() {
        List<InlineKeyboardButton> row = new ArrayList<>();
        row.add(InlineKeyboardButton.builder().text("◀").callbackData("wiz:back").build());
        row.add(InlineKeyboardButton.builder().text("▶").callbackData("wiz:next").build());
        row.add(InlineKeyboardButton.builder().text("✖").callbackData("wiz:cancel").build());
        return new InlineKeyboardMarkup(List.of(row));
    }

    private String toJson(WizardSession session) {
        try {
            return mapper.writeValueAsString(session);
        } catch (JsonProcessingException e) {
            return "";
        }
    }
}
