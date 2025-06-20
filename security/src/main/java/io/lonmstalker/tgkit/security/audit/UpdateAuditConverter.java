package io.lonmstalker.tgkit.security.audit;

import io.lonmstalker.tgkit.core.update.UpdateUtils;
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
