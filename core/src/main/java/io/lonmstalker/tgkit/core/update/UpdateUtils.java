package io.lonmstalker.tgkit.core.update;

import io.lonmstalker.tgkit.core.BotRequestType;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Predicate;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.boost.ChatBoostUpdated;

@UtilityClass
@SuppressWarnings({"method.invocation", "argument", "type.anno.before.modifier", "return"})
public class UpdateUtils {

  /** Таблица, сопоставляющая условие из Update типу запроса. */
  private static final Map<Predicate<Update>, BotRequestType> TYPE_MAP =
      new LinkedHashMap<>() {
        {
          put(u -> u.getMessage() != null, BotRequestType.MESSAGE);
          put(u -> u.getEditedMessage() != null, BotRequestType.EDITED_MESSAGE);
          put(u -> u.getChannelPost() != null, BotRequestType.CHANNEL_POST);
          put(u -> u.getEditedChannelPost() != null, BotRequestType.EDITED_CHANNEL_POST);
          put(u -> u.getShippingQuery() != null, BotRequestType.SHIPPING_QUERY);
          put(u -> u.getPreCheckoutQuery() != null, BotRequestType.PRE_CHECKOUT_QUERY);
          put(u -> u.getPoll() != null, BotRequestType.POLL);
          put(u -> u.getPollAnswer() != null, BotRequestType.POLL_ANSWER);
          put(u -> u.getChatMember() != null, BotRequestType.CHAT_MEMBER);
          put(u -> u.getMyChatMember() != null, BotRequestType.MY_CHAT_MEMBER);
          put(u -> u.getChatJoinRequest() != null, BotRequestType.CHAT_JOIN_REQUEST);
          put(u -> u.getCallbackQuery() != null, BotRequestType.CALLBACK_QUERY);
          put(u -> u.getInlineQuery() != null, BotRequestType.INLINE_QUERY);
          put(u -> u.getChosenInlineQuery() != null, BotRequestType.CHOSEN_INLINE_QUERY);
          put(u -> u.getMessageReaction() != null, BotRequestType.MESSAGE_REACTION);
          put(u -> u.getMessageReactionCount() != null, BotRequestType.MESSAGE_REACTION_COUNT);
          put(u -> u.getChatBoost() != null, BotRequestType.CHAT_BOOST);
          put(u -> u.getRemovedChatBoost() != null, BotRequestType.REMOVED_CHAT_BOOST);
        }
      };

  /** Функции, извлекающие пользователя из update. */
  private static final List<Function<Update, User>> USER_EXTRACTORS =
      List.of(
          u -> u.getMessage() != null ? u.getMessage().getFrom() : null,
          u -> u.getEditedMessage() != null ? u.getEditedMessage().getFrom() : null,
          u -> u.getChannelPost() != null ? u.getChannelPost().getFrom() : null,
          u -> u.getEditedChannelPost() != null ? u.getEditedChannelPost().getFrom() : null,
          u -> u.getCallbackQuery() != null ? u.getCallbackQuery().getFrom() : null,
          u -> u.getInlineQuery() != null ? u.getInlineQuery().getFrom() : null,
          u -> u.getChosenInlineQuery() != null ? u.getChosenInlineQuery().getFrom() : null,
          u -> u.getShippingQuery() != null ? u.getShippingQuery().getFrom() : null,
          u -> u.getPreCheckoutQuery() != null ? u.getPreCheckoutQuery().getFrom() : null,
          u -> u.getPollAnswer() != null ? u.getPollAnswer().getUser() : null,
          u -> u.getChatMember() != null ? u.getChatMember().getFrom() : null,
          u -> u.getMyChatMember() != null ? u.getMyChatMember().getFrom() : null,
          u -> u.getChatJoinRequest() != null ? u.getChatJoinRequest().getUser() : null);

  /** Определяет тип входящего update, используя таблицу соответствия предикатов и типов запроса. */
  public static @NonNull BotRequestType getType(@NonNull Update update) {
    return TYPE_MAP.entrySet().stream()
        .filter(e -> e.getKey().test(update))
        .findFirst()
        .map(Map.Entry::getValue)
        .orElseThrow(() -> new BotApiException("Unknown update type"));
  }

  /**
   * Attempts to extract {@link org.telegram.telegrambots.meta.api.objects.User} from update.
   *
   * @throws BotApiException when no user information can be resolved
   */
  public static @NonNull User getUser(@NonNull Update update) {
    return USER_EXTRACTORS.stream()
        .map(f -> f.apply(update))
        .filter(Objects::nonNull)
        .findFirst()
        .orElseThrow(() -> new BotApiException("User not found in update: " + update));
  }

  /** userId или {@code null}, если не применимо (анонимные реакции и т.д.). */
  public @Nullable Long resolveUserId(@NonNull Update update) {
    User u = getUserQuiet(update);
    return u != null ? u.getId() : null;
  }

  /**
   * Возвращает username пользователя (без @) или {@code null}, если он отсутствует / событие
   * анонимно.
   */
  public @Nullable String resolveUsername(@NonNull Update u) {
    User user = getUserQuiet(u);
    return user != null ? user.getUserName() : null;
  }

  /** Извлекает chat_id из {@link Update}. */
  public static @Nullable Long resolveChatId(@NonNull Update u) {
    if (u.getMessage() != null) return u.getMessage().getChatId();
    if (u.getEditedMessage() != null) return u.getEditedMessage().getChatId();
    if (u.getChannelPost() != null) return u.getChannelPost().getChatId();
    if (u.getEditedChannelPost() != null) return u.getEditedChannelPost().getChatId();
    if (u.getCallbackQuery() != null && u.getCallbackQuery().getMessage() != null)
      return u.getCallbackQuery().getMessage().getChatId();
    if (u.getChatMember() != null) return u.getChatMember().getChat().getId();
    if (u.getMyChatMember() != null) return u.getMyChatMember().getChat().getId();
    if (u.getChatJoinRequest() != null) return u.getChatJoinRequest().getChat().getId();
    if (u.getChatBoost() != null) {
      var boost = u.getChatBoost();
      return boost != null && boost.getChat() != null ? boost.getChat().getId() : null;
    }
    if (u.getRemovedChatBoost() != null) {
      var boost = u.getRemovedChatBoost();
      return boost != null && boost.getChat() != null ? boost.getChat().getId() : null;
    }
    // InlineQuery, PreCheckoutQuery, ShippingQuery и другие события не содержат chat_id.
    return null;
  }

  /** messageId или {@code null}. */
  public static @Nullable Integer resolveMessageId(@NonNull Update u) {
    if (u.getMessage() != null) return u.getMessage().getMessageId();
    if (u.getEditedMessage() != null) return u.getEditedMessage().getMessageId();
    if (u.getChannelPost() != null) return u.getChannelPost().getMessageId();
    if (u.getEditedChannelPost() != null) return u.getEditedChannelPost().getMessageId();
    if (u.getMessageReaction() != null) return u.getMessageReaction().getMessageId();
    if (u.getMessageReactionCount() != null) return u.getMessageReactionCount().getMessageId();
    if (u.getCallbackQuery() != null && u.getCallbackQuery().getMessage() != null)
      return u.getCallbackQuery().getMessage().getMessageId();
    /* Poll, PollAnswer, InlineQuery, ShippingQuery, Pre-Checkout и др. без messageId */
    return null;
  }

  /**
   * Пытается извлечь «основной» текст из Update. Это может быть: • text обычного сообщения / поста
   * <br>
   * • data коллбэка (Inline-кнопка)<br>
   * • query из InlineQuery / ChosenInlineQuery<br>
   * • question опроса / викторины<br>
   * • payload платёжного события (Invoice payload) Если текст отсутствует — возвращает {@code
   * null}.
   */
  public @Nullable String resolveText(@NonNull Update u) {
    if (u.getMessage() != null) return u.getMessage().getText();
    if (u.getEditedMessage() != null) return u.getEditedMessage().getText();
    if (u.getChannelPost() != null) return u.getChannelPost().getText();
    if (u.getEditedChannelPost() != null) return u.getEditedChannelPost().getText();

    if (u.getCallbackQuery() != null) return u.getCallbackQuery().getData();
    if (u.getInlineQuery() != null) return u.getInlineQuery().getQuery();
    if (u.getChosenInlineQuery() != null) return u.getChosenInlineQuery().getQuery();

    if (u.getPoll() != null) return u.getPoll().getQuestion();
    if (u.getShippingQuery() != null) return u.getShippingQuery().getInvoicePayload();
    if (u.getPreCheckoutQuery() != null) return u.getPreCheckoutQuery().getInvoicePayload();

    /* Boost / Reaction / Member updates не содержат текстового поля */
    return null;
  }

  private @Nullable User getUserQuiet(@NonNull Update u) {
    for (Function<Update, User> f : USER_EXTRACTORS) {
      User r = f.apply(u);
      if (r != null) return r;
    }
    return null;
  }

  private @Nullable Long chatId(@Nullable ChatBoostUpdated boost) {
    return boost != null && boost.getChat() != null ? boost.getChat().getId() : null;
  }
}
