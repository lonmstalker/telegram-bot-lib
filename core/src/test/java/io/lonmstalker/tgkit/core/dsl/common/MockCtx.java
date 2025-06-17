package io.lonmstalker.tgkit.core.dsl.common;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.BotService;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import io.lonmstalker.tgkit.core.user.BotUserInfo;

import java.util.Set;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class MockCtx {

    // фиктивные BotInfo / BotUserInfo
    public static DSLContext ctx(Long chatId, long userId, TelegramSender sender) {
        BotService bot = mock(BotService.class);
        when(bot.sender()).thenReturn(sender);
        when(bot.localizer()).thenReturn(mock(MessageLocalizer.class));

        BotUserInfo user = mock(BotUserInfo.class);
        when(user.chatId()).thenReturn(chatId);
        when(user.userId()).thenReturn(userId);
        when(user.roles()).thenReturn(Set.of("ADMIN"));

        return new DSLContext.SimpleDSLContext(bot, mock(BotInfo.class), user);
    }
}
