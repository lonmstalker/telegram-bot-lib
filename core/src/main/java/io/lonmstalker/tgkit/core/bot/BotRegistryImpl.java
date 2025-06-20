package io.lonmstalker.tgkit.core.bot;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Реестр активных Bot’ов с хранением WeakReference, чтобы при аварийном незакрытии они не держали
 * память.
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BotRegistryImpl implements BotRegistry {

  private static final BotRegistryImpl INSTANCE = new BotRegistryImpl();
  private final Map<Long, WeakReference<Bot>> byInternal = new ConcurrentHashMap<>();
  private final Map<Long, WeakReference<Bot>> byExternal = new ConcurrentHashMap<>();

  public static BotRegistryImpl getInstance() {
    return INSTANCE;
  }

  void register(@NonNull Bot bot) {
    byInternal.put(bot.internalId(), new WeakReference<>(bot));
    byExternal.put(bot.externalId(), new WeakReference<>(bot));
  }

  void unregister(@NonNull Bot bot) {
    byInternal.remove(bot.internalId());
    byExternal.remove(bot.externalId());
  }

  @Override
  @SuppressWarnings("methodref.return")
  public @NonNull Collection<Bot> all() {
    return byInternal.values().stream()
        .map(WeakReference::get)
        .filter(b -> b != null && BotState.RUNNING == b.state())
        .toList();
  }

  @Override
  public @NonNull Optional<Bot> getByInternalId(long id) {
    return Optional.ofNullable(byInternal.get(id)).map(WeakReference::get);
  }

  @Override
  public @NonNull Optional<Bot> getByExternalId(long id) {
    return Optional.ofNullable(byExternal.get(id)).map(WeakReference::get);
  }
}
