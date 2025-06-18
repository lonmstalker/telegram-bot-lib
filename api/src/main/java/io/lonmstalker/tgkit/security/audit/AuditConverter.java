package io.lonmstalker.tgkit.security.audit;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface AuditConverter {
    @NonNull AuditEvent convert(@NonNull Update src);
}
