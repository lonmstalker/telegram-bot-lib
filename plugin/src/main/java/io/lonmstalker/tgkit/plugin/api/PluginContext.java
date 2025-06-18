package io.lonmstalker.tgkit.plugin.api;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.security.audit.AuditBus;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public interface PluginContext {

    @Nullable
    <T> T getService(@NonNull Class<T> type);      // DI-адаптер

    @NonNull
    BotGlobalConfig config();             // доступ к глобальным настройкам

    @NonNull
    AuditBus audit();
}
