package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/**
 * Точка входа в DSL ответа бота.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotResponse {
    public static final BotResponse EMPTY = new BotResponse();

    /**
     * Конфигурирует глобальные параметры.
     */
    public static void config(@NonNull Consumer<DslGlobalConfig> cfg) {
        cfg.accept(DslGlobalConfig.INSTANCE);
    }

    /**
     * Контекст, привязанный к запросу.
     */
    public static @NonNull WithRequest with(@NonNull BotRequest<?> req) {
        return new WithRequest(req);
    }

    /**
     * Сообщение.
     */
    public static @NonNull MessageBuilder msg(@NonNull BotRequest<?> req, @NonNull String text) {
        return new MessageBuilder(req, text);
    }

    /**
     * Сообщение из i18n.
     */
    public static @NonNull MessageBuilder msgKey(@NonNull BotRequest<?> req, @NonNull String key, @NonNull Object... args) {
        MessageLocalizer loc = req.botInfo().localizer();
        return new MessageBuilder(req, loc.get(key, args));
    }

    /**
     * Фото.
     */
    public static @NonNull PhotoBuilder photo(@NonNull BotRequest<?> req, @NonNull InputFile file) {
        return new PhotoBuilder(req, file);
    }

    /**
     * Редактирование сообщения.
     */
    public static @NonNull EditBuilder edit(@NonNull BotRequest<?> req, long msgId) {
        return new EditBuilder(req, msgId);
    }

    /**
     * Удаление сообщения.
     */
    public static @NonNull DeleteBuilder delete(@NonNull BotRequest<?> req, long msgId) {
        return new DeleteBuilder(req, msgId);
    }

    /**
     * Отправка медиа-группы.
     */
    public static @NonNull MediaGroupBuilder mediaGroup(@NonNull BotRequest<?> req) {
        return new MediaGroupBuilder(req);
    }

    /**
     * Опрос.
     */
    public static @NonNull PollBuilder poll(@NonNull BotRequest<?> req, @NonNull String question) {
        return new PollBuilder(req, question);
    }

    /**
     * Викторина.
     */
    public static @NonNull QuizBuilder quiz(@NonNull BotRequest<?> req, @NonNull String question, int correct) {
        return new QuizBuilder(req, question, correct);
    }

    /**
     * Результаты инлайн-запроса.
     */
    public static @NonNull InlineResults inline(@NonNull BotRequest<?> req) {
        return new InlineResults(req);
    }

    /**
     * Построитель, привязанный к запросу.
     */
    public static final class WithRequest {
        private final @NonNull BotRequest<?> req;

        private WithRequest(@NonNull BotRequest<?> req) {
            this.req = req;
        }

        public @NonNull MessageBuilder msg(@NonNull String text) {
            return BotResponse.msg(req, text);
        }

        public @NonNull MessageBuilder msgKey(@NonNull String key, @NonNull Object... args) {
            return BotResponse.msgKey(req, key, args);
        }

        public @NonNull PhotoBuilder photo(@NonNull InputFile file) {
            return BotResponse.photo(req, file);
        }

        public @NonNull EditBuilder edit(long msgId) {
            return BotResponse.edit(req, msgId);
        }

        public @NonNull DeleteBuilder delete(long msgId) {
            return BotResponse.delete(req, msgId);
        }

        public @NonNull MediaGroupBuilder mediaGroup() {
            return BotResponse.mediaGroup(req);
        }

        public @NonNull PollBuilder poll(@NonNull String question) {
            return BotResponse.poll(req, question);
        }

        public @NonNull QuizBuilder quiz(@NonNull String question, int correct) {
            return BotResponse.quiz(req, question, correct);
        }

        public @NonNull InlineResults inline() {
            return BotResponse.inline(req);
        }
    }

    /**
     * Базовый строитель.
     */
    @SuppressWarnings("unchecked")
    static abstract class CommonBuilder<T extends CommonBuilder<T>> implements Common<T> {
        protected final @NonNull BotRequest<?> req;
        protected long chatId;
        protected Long replyTo;
        protected boolean disableNotif;
        protected KbBuilder keyboard;
        protected Duration ttl;
        protected Consumer<Long> success;
        protected Consumer<Throwable> error;
        protected Context ctx;

        CommonBuilder(@NonNull BotRequest<?> req) {
            this.req = req;
            this.chatId = Long.parseLong(req.user().chatId());
            this.ctx = new Context(chatId, req.user().roles());
        }

        @Override
        public @NonNull T chat(long id) {
            this.chatId = id;
            return (T) this;
        }

        @Override
        public @NonNull T replyTo(long msgId) {
            this.replyTo = msgId;
            return (T) this;
        }

        @Override
        public @NonNull T disableNotif() {
            this.disableNotif = true;
            return (T) this;
        }

        @Override
        public @NonNull T keyboard(@NonNull Consumer<KbBuilder> cfg) {
            KbBuilder kb = new KbBuilder(req.botInfo().localizer());
            cfg.accept(kb);
            this.keyboard = kb;
            return (T) this;
        }

        @Override
        public @NonNull T when(@NonNull Predicate<Context> cond,
                               @NonNull Consumer<Common<T>> branch) {
            if (ctx != null && cond.test(ctx)) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public @NonNull T onlyAdmin(@NonNull Consumer<Common<T>> branch) {
            if (ctx != null && ctx.isAdmin()) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public @NonNull T ifFlag(@NonNull String flag,
                                 @NonNull Context c,
                                 @NonNull Consumer<Common<T>> branch) {
            if (DslGlobalConfig.INSTANCE.getFlags().enabled(flag, c.chatId())) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public @NonNull T flag(@NonNull String flag,
                               @NonNull Context c,
                               @NonNull Consumer<Common<T>> branch) {
            return ifFlag(flag, c, branch);
        }

        @Override
        public @NonNull T ttl(@NonNull Duration duration) {
            this.ttl = duration;
            return (T) this;
        }

        @Override
        public @NonNull T hooks(@NonNull Consumer<Long> ok,
                                @NonNull Consumer<Throwable> fail) {
            this.success = ok;
            this.error = fail;
            return (T) this;
        }

        @Override
        public @NonNull BotResponse send(@NonNull TelegramTransport tg) {
            try {
                PartialBotApiMethod<?> m = build();
                long id = tg.execute(m);
                if (ttl != null) {
                    tg.scheduleDelete(chatId, id, ttl);
                }
                if (success != null) {
                    success.accept(id);
                }
                return BotResponse.EMPTY;
            } catch (Exception ex) {
                if (error != null) {
                    error.accept(ex);
                }
                throw new BotApiException(ex);
            }
        }

        @Override
        public @NonNull CompletableFuture<BotResponse> sendAsync(@NonNull Executor executor) {
            return CompletableFuture.supplyAsync(() -> send(DslGlobalConfig.INSTANCE.getTransport()), executor);
        }
    }
}
