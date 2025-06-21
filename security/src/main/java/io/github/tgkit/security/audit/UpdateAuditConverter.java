/*
 * Copyright 2025 TgKit Team
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
package io.github.tgkit.security.audit;

import io.github.tgkit.internal.update.UpdateUtils;
import java.time.Instant;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public final class UpdateAuditConverter implements AuditConverter {

  @Override
  public @NonNull AuditEvent convert(@NonNull Update u) {
    var type = UpdateUtils.getType(u);
    var userId = UpdateUtils.resolveUserId(u);
    return AuditEvent.builder()
        .data(Map.of())
        .timestamp(Instant.now())
        .category("BOT_" + type.name())
        .action(type.name().toLowerCase())
        .actor(userId != null ? "user:" + userId : "system")
        .build();
  }
}
