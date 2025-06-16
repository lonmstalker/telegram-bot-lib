package io.lonmstalker.tgkit.core.dsl;

import java.io.Serializable;
import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;
import java.util.function.Predicate;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.feature_flags.FeatureFlags;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.i18n.MessageLocalizer;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.PartialBotApiMethod;
import org.telegram.telegrambots.meta.api.objects.InaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;

/**
 * Точка входа в DSL ответа бота.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SuppressWarnings("initialization.fields.uninitialized")
public final class BotDSL {

    /**
     * Конфигурирует глобальные параметры.
     */
    public static void config(@NonNull Consumer<DslGlobalConfig> cfg) {
        cfg.accept(DslGlobalConfig.INSTANCE);
    }

    /**
     * Контекст, привязанный к запросу.
     */
    public static @NonNull WithRequest with(@NonNull DSLContext ctx) {
        return new WithRequest(ctx);
    }

    /**
     * Сообщение.
     */
    public static @NonNull MessageBuilder msg(@NonNull DSLContext ctx, @NonNull String text) {
        return new MessageBuilder(ctx, text);
    }

    /**
     * Сообщение из i18n.
     */
    public static @NonNull MessageBuilder msgKey(@NonNull DSLContext ctx, @NonNull String key, @NonNull Object... args) {
        MessageLocalizer loc = ctx.botInfo().localizer();
        return new MessageBuilder(ctx, loc.get(key, args));
    }

    /**
     * Фото.
     */
    public static @NonNull PhotoBuilder photo(@NonNull DSLContext ctx, @NonNull InputFile file) {
        return new PhotoBuilder(ctx, file);
    }

    /**
     * Редактирование сообщения.
     */
    public static @NonNull EditBuilder edit(@NonNull DSLContext ctx, long msgId) {
        return new EditBuilder(ctx, msgId);
    }

    /**
     * Удаление сообщения.
     */
    public static @NonNull DeleteBuilder delete(@NonNull DSLContext ctx, long msgId) {
        return new DeleteBuilder(ctx, msgId);
    }

    /**
     * Отправка медиа-группы.
     */
    public static @NonNull MediaGroupBuilder mediaGroup(@NonNull DSLContext ctx) {
        return new MediaGroupBuilder(ctx);
    }

    /**
     * Опрос.
     */
    public static @NonNull PollBuilder poll(@NonNull DSLContext ctx, @NonNull String question) {
        return new PollBuilder(ctx, question);
    }

    /**
     * Викторина.
     */
    public static @NonNull QuizBuilder quiz(@NonNull DSLContext ctx, @NonNull String question, int correct) {
        return new QuizBuilder(ctx, question, correct);
    }

    /**
     * Результаты инлайн-запроса.
     */
    public static @NonNull InlineResults inline(@NonNull DSLContext ctx) {
        return new InlineResults(ctx);
    }

    /**
     * Контекст, привязанный к запросу.
     */
    public static @NonNull WithRequest with(@NonNull BotRequest<?> req) {
        return new WithRequest(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()));
    }

    /**
     * Сообщение.
     */
    public static @NonNull MessageBuilder msg(@NonNull BotRequest<?> req, @NonNull String text) {
        return new MessageBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), text);
    }

    /**
     * Сообщение из i18n.
     */
    public static @NonNull MessageBuilder msgKey(@NonNull BotRequest<?> req, @NonNull String key, @NonNull Object... args) {
        MessageLocalizer loc = req.botInfo().localizer();
        return new MessageBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), loc.get(key, args));
    }

    /**
     * Фото.
     */
    public static @NonNull PhotoBuilder photo(@NonNull BotRequest<?> req, @NonNull InputFile file) {
        return new PhotoBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), file);
    }

    /**
     * Редактирование сообщения.
     */
    public static @NonNull EditBuilder edit(@NonNull BotRequest<?> req, long msgId) {
        return new EditBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), msgId);
    }

    /**
     * Удаление сообщения.
     */
    public static @NonNull DeleteBuilder delete(@NonNull BotRequest<?> req, long msgId) {
        return new DeleteBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), msgId);
    }

    /**
     * Отправка медиа-группы.
     */
    public static @NonNull MediaGroupBuilder mediaGroup(@NonNull BotRequest<?> req) {
        return new MediaGroupBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()));
    }

    /**
     * Опрос.
     */
    public static @NonNull PollBuilder poll(@NonNull BotRequest<?> req, @NonNull String question) {
        return new PollBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), question);
    }

    /**
     * Викторина.
     */
    public static @NonNull QuizBuilder quiz(@NonNull BotRequest<?> req, @NonNull String question, int correct) {
        return new QuizBuilder(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()), question, correct);
    }

    /**
     * Результаты инлайн-запроса.
     */
    public static @NonNull InlineResults inline(@NonNull BotRequest<?> req) {
        return new InlineResults(new DSLContext.SimpleDSLContext(req.botInfo(), req.user()));
    }

    /**
     * Построитель, привязанный к запросу.
     */
    public static final class WithRequest {
        private final @NonNull DSLContext ctx;

        private WithRequest(@NonNull DSLContext ctx) {
            this.ctx = ctx;
        }

        public @NonNull MessageBuilder msg(@NonNull String text) {
            return BotDSL.msg(ctx, text);
        }

        public @NonNull MessageBuilder msgKey(@NonNull String key, @NonNull Object... args) {
            return BotDSL.msgKey(ctx, key, args);
        }

        public @NonNull PhotoBuilder photo(@NonNull InputFile file) {
            return BotDSL.photo(ctx, file);
        }

        public @NonNull EditBuilder edit(long msgId) {
            return BotDSL.edit(ctx, msgId);
        }

        public @NonNull DeleteBuilder delete(long msgId) {
            return BotDSL.delete(ctx, msgId);
        }

        public @NonNull MediaGroupBuilder mediaGroup() {
            return BotDSL.mediaGroup(ctx);
        }

        public @NonNull PollBuilder poll(@NonNull String question) {
            return BotDSL.poll(ctx, question);
        }

        public @NonNull QuizBuilder quiz(@NonNull String question, int correct) {
            return BotDSL.quiz(ctx, question, correct);
        }

        public @NonNull InlineResults inline() {
            return BotDSL.inline(ctx);
        }
    }

    /**
     * Базовый строитель.
     */
    @SuppressWarnings("unchecked")
    static abstract class CommonBuilder<T extends CommonBuilder<T>> implements Common<T> {
        protected @Nullable Long chatId;
        protected Long replyTo;
        protected boolean disableNotif;
        protected KbBuilder keyboard;
        protected Duration ttl;
        protected Consumer<Long> success;
        protected Consumer<Throwable> error;
        protected DSLContext ctx;
        protected @Nullable MissingIdStrategy missingIdStrategy;

        CommonBuilder(@NonNull DSLContext ctx) {
            this.ctx = ctx;
            this.chatId = ctx.userInfo().chatId();
        }

        @Override
        public @NonNull T missingIdStrategy(@NonNull MissingIdStrategy strategy) {
            this.missingIdStrategy = strategy;
            return (T) this;
        }

        @Override
        public @NonNull T requireChatId() {
            if (ctx.userInfo().chatId() == null) {
                MissingIdStrategy s = missingIdStrategy != null
                        ? missingIdStrategy
                        : DslGlobalConfig.INSTANCE.getMissingIdStrategy();
                s.onMissing("chatId", ctx);
            }
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
            KbBuilder kb = new KbBuilder(ctx.botInfo().localizer());
            cfg.accept(kb);
            this.keyboard = kb;
            return (T) this;
        }

        @Override
        public @NonNull T when(@NonNull Predicate<DSLContext> cond,
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
        @SuppressWarnings("unboxing.of.nullable")
        public @NonNull T ifFlag(@NonNull String flag,
                                 @NonNull Consumer<Common<T>> branch) {
            if (chatId == null) {
                return (T) this;
            }
            if (DslGlobalConfig.INSTANCE.getFlags().enabled(flag, chatId)) {
                branch.accept(this);
            }
            return (T) this;
        }

        @Override
        public @NonNull T flag(@NonNull String flag,
                               @NonNull Consumer<Common<T>> branch) {
            return ifFlag(flag, branch);
        }

        @Override
        public @NonNull T abTest(@NonNull String key,
                                 @NonNull Consumer<Common<T>> control,
                                 @NonNull Consumer<Common<T>> variant) {
            Long entityId = ctx.userInfo().chatId() != null
                    ? ctx.userInfo().chatId()
                    : ctx.userInfo().userId();

            if (entityId == null) {
                if (missingIdStrategy != null) {
                    missingIdStrategy.onMissing("chatId,userId", ctx);
                }
                return (T) this;
            }

            FeatureFlags.Variant v =
                    DslGlobalConfig.INSTANCE.getFlags().variant(key, entityId);

            if (v == FeatureFlags.Variant.VARIANT) {
                variant.accept(this);
            } else if (v != null) {
                control.accept(this);
            }

            return (T) this;
        }

        @Override
        public @NonNull T flagUser(@NonNull String flag,
                                   @NonNull Consumer<Common<T>> branch) {
            Long uid = ctx.userInfo().userId();
            if (uid != null &&
                    DslGlobalConfig.INSTANCE.getFlags().enabledForUser(flag, uid)) {
                branch.accept(this);
            }
            return (T) this;
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
        public @NonNull BotResponse send() {
            try {
                PartialBotApiMethod<?> m = build();
                Serializable response = ctx.botInfo().sender().execute(m);

                if (response instanceof Message msg) {
                    success.accept(Long.valueOf(msg.getMessageId()));
                } else if (response instanceof InaccessibleMessage msg) {
                    success.accept(Long.valueOf(msg.getMessageId()));
                }
                return new BotResponse();
            } catch (Exception ex) {
                if (error != null) {
                    error.accept(ex);
                }
                throw new BotApiException(ex);
            }
        }

        @Override
        public @NonNull CompletableFuture<BotResponse> sendAsync(@NonNull Executor executor) {
            return CompletableFuture.supplyAsync(this::send, executor);
        }
    }
}
