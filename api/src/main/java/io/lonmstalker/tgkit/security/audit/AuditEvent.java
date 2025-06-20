package io.lonmstalker.tgkit.security.audit;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.time.Instant;
import java.util.Map;
import org.checkerframework.checker.nullness.qual.NonNull;

@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AuditEvent {
  private final String category; // e.g. "SECURITY", "BOT_COMMAND"
  private final String actor; // "user:123", "chat:456", "system"
  private final String action; // свободный текст / template id
  private final Instant timestamp;
  private final Map<String, Object> data;

  private AuditEvent(Builder builder) {
    this.category = builder.category;
    this.actor = builder.actor;
    this.action = builder.action;
    this.timestamp = builder.timestamp;
    this.data = builder.data;
  }

  public String getCategory() {
    return category;
  }

  public String getActor() {
    return actor;
  }

  public String getAction() {
    return action;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Map<String, Object> getData() {
    return data;
  }

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {
    private String category;
    private String actor;
    private String action;
    private Instant timestamp;
    private Map<String, Object> data;

    public Builder category(String category) {
      this.category = category;
      return this;
    }

    public Builder actor(String actor) {
      this.actor = actor;
      return this;
    }

    public Builder action(String action) {
      this.action = action;
      return this;
    }

    public Builder timestamp(Instant timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder data(Map<String, Object> data) {
      this.data = data;
      return this;
    }

    public AuditEvent build() {
      return new AuditEvent(this);
    }
  }

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
