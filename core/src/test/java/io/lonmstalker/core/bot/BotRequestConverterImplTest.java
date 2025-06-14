import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.bot.BotRequestConverterImpl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.*;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("BotRequestConverterImpl")
class BotRequestConverterImplTest {
    @Test
    @DisplayName("маппит все типы запросов")
    void shouldMapAllTypes() {
        BotRequestConverterImpl conv = new BotRequestConverterImpl();
        Update u = new Update();

        Message m = new Message();
        u.setMessage(m);
        assertEquals(m, conv.convert(u, BotRequestType.MESSAGE));

        u = new Update();
        Message em = new Message();
        u.setEditedMessage(em);
        assertEquals(em, conv.convert(u, BotRequestType.EDITED_MESSAGE));

        u = new Update();
        Message cp = new Message();
        u.setChannelPost(cp);
        assertEquals(cp, conv.convert(u, BotRequestType.CHANNEL_POST));

        u = new Update();
        Message ecp = new Message();
        u.setEditedChannelPost(ecp);
        assertEquals(ecp, conv.convert(u, BotRequestType.EDITED_CHANNEL_POST));

        u = new Update();
        ShippingQuery sq = new ShippingQuery();
        u.setShippingQuery(sq);
        assertEquals(sq, conv.convert(u, BotRequestType.SHIPPING_QUERY));

        u = new Update();
        PreCheckoutQuery pcq = new PreCheckoutQuery();
        u.setPreCheckoutQuery(pcq);
        assertEquals(pcq, conv.convert(u, BotRequestType.PRE_CHECKOUT_QUERY));

        u = new Update();
        Poll poll = new Poll();
        u.setPoll(poll);
        assertEquals(poll, conv.convert(u, BotRequestType.POLL));

        u = new Update();
        PollAnswer pa = new PollAnswer();
        u.setPollAnswer(pa);
        assertEquals(pa, conv.convert(u, BotRequestType.POLL_ANSWER));

        u = new Update();
        ChatMemberUpdated cmu = new ChatMemberUpdated();
        u.setChatMember(cmu);
        assertEquals(cmu, conv.convert(u, BotRequestType.CHAT_MEMBER));

        u = new Update();
        ChatMemberUpdated mcmu = new ChatMemberUpdated();
        u.setMyChatMember(mcmu);
        assertEquals(mcmu, conv.convert(u, BotRequestType.MY_CHAT_MEMBER));

        u = new Update();
        ChatJoinRequest cjr = new ChatJoinRequest();
        u.setChatJoinRequest(cjr);
        assertEquals(cjr, conv.convert(u, BotRequestType.CHAT_JOIN_REQUEST));

        u = new Update();
        CallbackQuery cq = new CallbackQuery();
        u.setCallbackQuery(cq);
        assertEquals(cq, conv.convert(u, BotRequestType.CALLBACK_QUERY));

        u = new Update();
        InlineQuery iq = new InlineQuery();
        u.setInlineQuery(iq);
        assertEquals(iq, conv.convert(u, BotRequestType.INLINE_QUERY));

        u = new Update();
        ChosenInlineQuery ciq = new ChosenInlineQuery();
        u.setChosenInlineQuery(ciq);
        assertEquals(ciq, conv.convert(u, BotRequestType.CHOSEN_INLINE_QUERY));

        u = new Update();
        MessageReactionUpdated mru = new MessageReactionUpdated();
        u.setMessageReaction(mru);
        assertEquals(mru, conv.convert(u, BotRequestType.MESSAGE_REACTION));

        u = new Update();
        MessageReactionCountUpdated mrcu = new MessageReactionCountUpdated();
        u.setMessageReactionCount(mrcu);
        assertEquals(mrcu, conv.convert(u, BotRequestType.MESSAGE_REACTION_COUNT));

        u = new Update();
        ChatBoostUpdated boost = new ChatBoostUpdated();
        u.setChatBoost(boost);
        assertEquals(boost, conv.convert(u, BotRequestType.CHAT_BOOST));

        u = new Update();
        ChatBoostUpdated removed = new ChatBoostUpdated();
        u.setRemovedChatBoost(removed);
        assertEquals(removed, conv.convert(u, BotRequestType.REMOVED_CHAT_BOOST));
    }
}
