/*
 * Copyright (C) 2024 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import io.lonmstalker.tgkit.core.reflection.ReflectionUtils;
import io.lonmstalker.tgkit.security.config.BotSecurityGlobalConfig;
import java.lang.reflect.*;
import java.util.*;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
