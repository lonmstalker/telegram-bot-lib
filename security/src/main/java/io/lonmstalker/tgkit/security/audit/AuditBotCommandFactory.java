package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.core.reflection.ReflectionUtils;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.lang.reflect.*;
import java.util.*;

@Slf4j
public final class AuditBotCommandFactory implements BotCommandFactory<Audit> {
    private static final AuditConverter DEFAULT = new UpdateAuditConverter();

    @Override
    public @NonNull Class<Audit> annotationType() {
        return Audit.class;
    }

    @Override
    public void apply(@NonNull BotCommand<?> command,
                      @NonNull Method m,
                      @Nullable Audit audit) {
        command.addInterceptor(create(Objects.requireNonNull(audit, "audit required")));
    }

    private @NonNull BotInterceptor create(@NonNull Audit audit) {
        AuditConverter conv;
        if (audit.converter() != null) {
            conv = ReflectionUtils.newInstance(audit.converter());
        } else {
            conv = DEFAULT;
        }
        return new DelegatingAuditInterceptor(BotSecurityGlobalConfig.INSTANCE.audit().bus(), conv);
    }
}
