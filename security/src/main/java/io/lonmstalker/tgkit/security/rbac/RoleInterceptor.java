package io.lonmstalker.tgkit.security.rbac;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.BotResponse;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Set;

final class RoleInterceptor implements BotInterceptor {
    private final Set<String> allowed;

    RoleInterceptor(Set<String> allowed) {
        this.allowed = allowed;
    }

    @Override
    public void preHandle(@NonNull Update upd, @NonNull BotRequest<?> request) {
        BotUserInfo u = request.user();
        if (u.roles().stream().noneMatch(allowed::contains)) {
            throw new ForbiddenException("role required: " + allowed);
        }
    }

    @Override
    public void postHandle(@NonNull Update u, @NonNull BotRequest<?> request) {
    }

    @Override
    public void afterCompletion(@NonNull Update u,
                                @NonNull BotRequest<?> req,
                                @Nullable BotResponse r,
                                @Nullable Exception e) {
    }
}