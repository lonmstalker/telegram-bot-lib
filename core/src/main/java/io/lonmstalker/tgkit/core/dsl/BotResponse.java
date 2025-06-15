package io.lonmstalker.tgkit.core.dsl;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import io.lonmstalker.tgkit.core.BotRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaVideo;

/**
 * Точка входа в DSL ответа бота.
 */
public final class BotResponse {

    private static final Logger log = LoggerFactory.getLogger(BotResponse.class);

    /** Глобальная конфигурация. */
    private static final Config CONFIG = new Config();

    private BotResponse() {}

    /** Конфигурирует глобальные параметры. */
    public static void config(Consumer<Config> cfg) {
        cfg.accept(CONFIG);
    }

    /** Сообщение. */
    public static MessageBuilder msg(String text) {
        return new MessageBuilder(text);
    }

    /** Сообщение из i18n. */
    public static MessageBuilder msgKey(String key, Function<String, Object[], String> i18n, Object... args) {
        return new MessageBuilder(i18n.apply(key, args));
    }

    /** Фото. */
    public static PhotoBuilder photo(InputFile file) {
        return new PhotoBuilder(file);
    }

    /** Редактирование сообщения. */
    public static EditBuilder edit(long msgId) {
        return new EditBuilder(msgId);
    }

    /** Удаление сообщения. */
    public static DeleteBuilder delete(long msgId) {
        return new DeleteBuilder(msgId);
    }

    /** Отправка медиа-группы. */
    public static MediaGroupBuilder mediaGroup() {
        return new MediaGroupBuilder();
    }

    /** Опрос. */
    public static PollBuilder poll(String question) {
        return new PollBuilder(question);
    }

    /** Викторина. */
    public static QuizBuilder quiz(String question, int correct) {
        return new QuizBuilder(question, correct);
    }

    /** Результаты инлайн-запроса. */
    public static InlineResults inline() {
        return new InlineResults();
    }

    /** Базовый строитель. */
    @SuppressWarnings("unchecked")
    static abstract class CommonBuilder<T extends CommonBuilder<T>> implements Common<T> {
        protected long chatId;
        protected Long replyTo;
        protected boolean disableNotif;
        protected KbBuilder keyboard;
        protected Duration ttl;
        protected Consumer<Long> success;
        protected Consumer<Throwable> error;
        protected Context ctx;

        @Override
        public T chat(long id) {
            this.chatId = id;
            return (T) this;
        }

        @Override
        public T autoChat(BotRequest<?> req) {
            this.chatId = Long.parseLong(req.user().chatId());
            this.ctx = new Context(chatId, req.user().roles());
            return (T) this;
        }

        @Override
        public T replyTo(long msgId) {
            this.replyTo = msgId;
            return (T) this;
        }

        @Override
        public T disableNotif() {
            this.disableNotif = true;
            return (T) this;
        }

        @Override
        public T keyboard(Consumer<KbBuilder> cfg) {
            KbBuilder kb = new KbBuilder();
            cfg.accept(kb);
            this.keyboard = kb;
            return (T) this;
        }

        @Override
        public T when(Predicate<Context> cond, Consumer<? extends Common<?>> branch) {
            if (ctx != null && cond.test(ctx)) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public T onlyAdmin(Consumer<? extends Common<?>> branch) {
            if (ctx != null && ctx.isAdmin()) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public T ifFlag(String flag, Context c, Consumer<? extends Common<?>> branch) {
            if (CONFIG.flags.enabled(flag, c.chatId())) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public T flag(String flag, Context c, Consumer<? extends Common<?>> branch) {
            return ifFlag(flag, c, branch);
        }

        @Override
        public T ttl(Duration duration) {
            this.ttl = duration;
            return (T) this;
        }

        @Override
        public T hooks(Consumer<Long> ok, Consumer<Throwable> fail) {
            this.success = ok;
            this.error = fail;
            return (T) this;
        }

        @Override
        public BotResponse send(TelegramTransport tg) {
            try {
                BotApiMethod<?> m = build();
                long id = tg.execute(m);
                if (ttl != null) {
                    tg.scheduleDelete(chatId, id, ttl);
                }
                if (success != null) {
                    success.accept(id);
                }
                return new BotResponse();
            } catch (Exception ex) {
                if (error != null) {
                    error.accept(ex);
                }
                throw ex instanceof RuntimeException r ? r : new RuntimeException(ex);
            }
        }

        @Override
        public CompletableFuture<BotResponse> sendAsync(Executor executor) {
            return CompletableFuture.supplyAsync(() -> send(CONFIG.transport), executor);
        }

        protected abstract BotApiMethod<?> build();
    }

    /** Настройки по умолчанию. */
    public static final class Config {
        private String parseMode = ParseMode.HTML;
        private boolean sanitize;
        private FeatureFlags flags = FeatureFlags.noop();
        private TelegramTransport transport = new TelegramTransport() {
            @Override
            public long execute(BotApiMethod<?> method) {
                log.debug("execute: {}", method); return 0;
            }
            @Override
            public void delete(long chatId, long messageId) {
                log.debug("delete: {}", messageId);
            }
        };

        public Config markdownV2() {
            this.parseMode = ParseMode.MARKDOWNV2;
            return this;
        }

        public Config sanitizeMarkdown() {
            this.sanitize = true;
            return this;
        }

        public Config featureFlags(FeatureFlags flags) {
            this.flags = flags;
            return this;
        }

        public Config transport(TelegramTransport tr) {
            this.transport = tr;
            return this;
        }
    }
}
