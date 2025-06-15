package io.lonmstalker.tgkit.security;

import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import java.text.MessageFormat;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;

public class MathCaptcha implements CaptchaProvider {
    private final Map<Long, Integer> answers = new ConcurrentHashMap<>();
    private final Random rnd = new Random();

    public static MathCaptcha easy() {
        return new MathCaptcha();
    }

    @Override
    public SendMessage question(long chatId, MessageLocalizer localizer) {
        int a = rnd.nextInt(10);
        int b = rnd.nextInt(10);
        int answer = a + b;
        int wrong = answer + rnd.nextInt(3) + 1;
        answers.put(chatId, answer);

        String text = MessageFormat.format(
                localizer.get("captcha.math.question"), a, b);

        InlineKeyboardButton okBtn = new InlineKeyboardButton(String.valueOf(answer));
        okBtn.setCallbackData(String.valueOf(answer));
        InlineKeyboardButton wrongBtn = new InlineKeyboardButton(String.valueOf(wrong));
        wrongBtn.setCallbackData(String.valueOf(wrong));

        InlineKeyboardMarkup markup = new InlineKeyboardMarkup(
                List.of(List.of(okBtn, wrongBtn)));

        SendMessage msg = new SendMessage(Long.toString(chatId), text);
        msg.setReplyMarkup(markup);
        return msg;
    }

    @Override
    public boolean verify(long chatId, String answer) {
        Integer expected = answers.get(chatId);
        return expected != null && Integer.toString(expected).equals(answer);
    }
}
