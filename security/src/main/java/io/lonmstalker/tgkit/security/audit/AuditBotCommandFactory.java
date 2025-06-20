package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.core.reflection.ReflectionUtils;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import java.lang.reflect.*;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class AuditBotCommandFactory implements BotCommandFactory<Audit> {
  private static final Logger log = LoggerFactory.getLogger(AuditBotCommandFactory.class);
  private static final AuditConverter DEFAULT = new UpdateAuditConverter();

  @Override
  public @NonNull Class<Audit> annotationType() {
    return Audit.class;
  }

  @Override
  public void apply(@NonNull BotCommand<?> command, @NonNull Method m, @Nullable Audit audit) {
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
