package io.github.tgkit.flag;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import io.github.tgkit.internal.BotInfo;
import io.github.tgkit.internal.BotService;
import io.github.tgkit.internal.bot.TelegramSender;
import io.github.tgkit.internal.dsl.MessageBuilder;
import io.github.tgkit.internal.dsl.context.DSLContext;
import io.github.tgkit.internal.i18n.MessageLocalizer;
import io.github.tgkit.internal.user.BotUserInfo;
import java.util.Set;
import net.jqwik.api.Arbitrary;
import net.jqwik.api.Arbitraries;
import net.jqwik.api.Combinators;
import net.jqwik.api.ForAll;
import net.jqwik.api.Provide;
import net.jqwik.api.Property;

/**
 * Property-based tests for message generation.
 */
class TgJqwik {

  /** Provides random {@link DSLContext} instances. */
  @Provide
  Arbitrary<DSLContext> contexts() {
    Arbitrary<Long> chatId = Arbitraries.longs().between(1L, 100_000L);
    Arbitrary<Long> userId = Arbitraries.longs().between(1L, 100_000L);
    return Combinators.combine(chatId, userId)
        .as(
            (c, u) -> {
              BotService service = mock(BotService.class);
              when(service.sender()).thenReturn(mock(TelegramSender.class));
              when(service.localizer()).thenReturn(mock(MessageLocalizer.class));
              when(service.userKVStore()).thenReturn(null);
              when(service.store()).thenReturn(null);
              BotUserInfo user = mock(BotUserInfo.class);
              when(user.chatId()).thenReturn(c);
              when(user.userId()).thenReturn(u);
              when(user.roles()).thenReturn(Set.of("ADMIN"));
              return new DSLContext.SimpleDSLContext(service, new BotInfo(1L), user);
            });
  }

  /** Provides short random text messages. */
  @Provide
  Arbitrary<String> messages() {
    return Arbitraries.strings().withCharRange(' ', '~').ofMinLength(1).ofMaxLength(100);
  }

  /**
   * Builds messages with random inputs and expects no exceptions.
   *
   * <p>Example:
   *
   * <pre>{@code
   * jqwik.api.Property property = new TgJqwik().messagesDoNotThrow();
   * }</pre>
   */
  @Property(tries = 50)
  void messagesDoNotThrow(@ForAll("contexts") DSLContext ctx, @ForAll("messages") String text) {
    new MessageBuilder(ctx, text).build();
  }
}
