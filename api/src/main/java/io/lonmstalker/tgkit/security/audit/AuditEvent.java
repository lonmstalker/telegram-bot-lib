package io.lonmstalker.tgkit.security.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Getter;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.time.Instant;
import java.util.Map;

@Builder
@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AuditEvent {
    private final String category;   // e.g. "SECURITY", "BOT_COMMAND"
    private final String actor;      // "user:123", "chat:456", "system"
    private final String action;     // свободный текст / template id
    private final Instant timestamp;
    private final Map<String, Object> data;

    /* Фабрики для самых частых сценариев — лаконичнее в коде. */
    public static @NonNull AuditEvent userAction(long userId, @NonNull String action) {
        return AuditEvent.builder()
                .action(action)
                .category("BOT_COMMAND")
                .actor("user:" + userId)
                .data(Map.of())
                .build();
    }

    public static @NonNull AuditEvent securityAlert(@NonNull String actor, @NonNull String msg) {
        return AuditEvent.builder()
                .action(msg)
                .category("SECURITY")
                .actor(actor)
                .data(Map.of())
                .build();
    }
}