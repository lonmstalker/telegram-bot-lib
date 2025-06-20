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
package io.lonmstalker.tgkit.core.bot;

import java.lang.ref.WeakReference;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Реестр активных Bot’ов с хранением WeakReference, чтобы при аварийном незакрытии они не держали
 * память.
 */
public final class BotRegistryImpl implements BotRegistry {
  private BotRegistryImpl() {}

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
