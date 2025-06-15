package io.lonmstalker.tgkit.render.telegram;

import io.craftbot.render.spi.ResponseDispatcher;
import io.lonmstalker.tgkit.core.BotInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.args.Context;
import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.state.InMemoryStateStore;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class ResponseDispatcherTest {

    private Context createContext() {
        BotConfig cfg = BotConfig.builder().build();
        TelegramSender sender = new TelegramSender(cfg, "token");
        BotInfo info = new BotInfo(1L, new InMemoryStateStore(), sender, new MessageLocalizer());
        BotUserInfo user = new BotUserInfo() {
            @Override
            public @NonNull String chatId() { return "1"; }
            @Override
            public @NonNull Set<String> roles() { return Set.of(); }
        };
        BotRequest<Update> req = new BotRequest<>(1, new Update(), info, user);
        return new Context(req, null);
    }

    @Test
    void text_renderer_selected() throws Exception {
        ResponseDispatcher d = new ResponseDispatcher(getClass().getClassLoader());
        BotResponse r = d.toResponse(new MarkdownMsg("hi"), createContext());
        assertTrue(r.getMethod() instanceof SendMessage);
    }

    @Test
    void photo_renderer_selected() throws Exception {
        ResponseDispatcher d = new ResponseDispatcher(getClass().getClassLoader());
        PhotoMsg msg = new PhotoMsg(new InputFile("id"), "cap");
        BotResponse r = d.toResponse(msg, createContext());
        assertTrue(r.getMethod() instanceof SendPhoto);
    }

    @Test
    void no_renderer_error() {
        ResponseDispatcher d = new ResponseDispatcher(getClass().getClassLoader());
        assertThrows(IllegalArgumentException.class, () -> d.toResponse(42, createContext()));
    }
}
