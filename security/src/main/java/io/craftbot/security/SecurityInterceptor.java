package io.craftbot.security;

import io.craftbot.security.spi.Key;
import io.craftbot.security.spi.RoleResolver;
import io.craftbot.security.spi.RateLimitBackend;
import io.craftbot.security.spi.CaptchaProvider;
import io.craftbot.security.spi.StateStore;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

public class SecurityInterceptor implements BotInterceptor {
    private final RoleResolver roles;
    private final RateLimiter limiter;
    private final CaptchaGuard captcha;

    public SecurityInterceptor(RoleResolver roles,
                               RateLimitBackend backend,
                               CaptchaProvider provider,
                               StateStore store) {
        this.roles = roles;
        this.limiter = new RateLimiter(backend);
        this.captcha = new CaptchaGuard(provider, store);
    }

    @Override
    public void preHandle(@NonNull Update update) {
        // nothing
    }

    @Override
    public void postHandle(@NonNull Update update) {
        // nothing
    }

    @Override
    public void afterCompletion(@NonNull Update update, @Nullable BotResponse response, @Nullable Exception ex) throws Exception {
        // nothing
    }

    public void handle(Update update, java.lang.reflect.Method method) {
        Roles rolesAnn = method.getAnnotation(Roles.class);
        if (rolesAnn != null) {
            long userId = update.getMessage().getFrom().getId();
            for (String role : rolesAnn.value()) {
                if (!roles.hasRole(userId, role)) {
                    throw new ForbiddenException("role");
                }
            }
        }

        RateLimit[] rlAnn = method.getAnnotationsByType(RateLimit.class);
        for (RateLimit rl : rlAnn) {
            Key key = new Key(rl.key().name()+":"+update.getMessage().getFrom().getId());
            if (!limiter.tryAcquire(key, rl.permits(), rl.seconds())) {
                captcha.challengeOrThrow(update);
                return;
            }
        }
    }
}
