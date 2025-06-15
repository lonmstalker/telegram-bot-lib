package io.craftbot.security;

import io.craftbot.security.spi.CaptchaChallenge;
import io.craftbot.security.spi.CaptchaProvider;
import io.craftbot.security.spi.StateStore;
import org.telegram.telegrambots.meta.api.objects.Update;

/** Simple guard wrapping captcha provider. */
public class CaptchaGuard {
    private final CaptchaProvider provider;
    private final StateStore store;

    public CaptchaGuard(CaptchaProvider provider, StateStore store) {
        this.provider = provider;
        this.store = store;
    }

    public void challengeOrThrow(Update update) {
        long chat = update.getMessage().getChatId();
        if (store.get(chat, "captchaOK") != null) {
            return;
        }
        CaptchaChallenge c = provider.create(chat);
        // Usually you would send a message; here we just throw exception
        throw new CaptchaRequired(c);
    }

    public void onCallback(long chat, String data) {
        if (provider.verify(chat, data)) {
            store.set(chat, "captchaOK", "yes", 60);
        }
    }
}
