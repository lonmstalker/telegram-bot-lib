package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.dsl.common.MockCtx;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.methods.polls.SendPoll;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

class QuizBuilderEdgeTest {

    static {
        BotCoreInitializer.init();
    }

    @Test
    void quizCorrectIdAndValidation() throws TelegramApiException {
        TelegramSender sender = mock(TelegramSender.class);
        doReturn(null)
                .when(sender)
                .execute(Mockito.<PartialBotApiMethod<?>>any());

        DSLContext ctx = MockCtx.ctx(77L, 88L, sender);

        var qb = new QuizBuilder(ctx, "2+2=?", 1)
                .option("3").option("4").option("5");

        SendPoll poll = (SendPoll) qb.build();

        assertThat(poll.getType()).isEqualTo("quiz");
        assertThat(poll.getCorrectOptionId()).isEqualTo(1);
        assertThat(poll.getChatId()).isEqualTo("77");
    }

    @Test
    void quizWithoutChatThrows() throws TelegramApiException {
        TelegramSender sender = mock(TelegramSender.class);
        doReturn(null)
                .when(sender)
                .execute(Mockito.<PartialBotApiMethod<?>>any());

        DSLContext ctx = MockCtx.ctx(/*chat=*/null, 99L, sender);

        var qb = BotDSL.quiz(ctx, "q", 0)
                .missingIdStrategy(MissingIdStrategy.ERROR);

        assertThatThrownBy(qb::build).isInstanceOfAny(RuntimeException.class, BotApiException.class);
    }
}
