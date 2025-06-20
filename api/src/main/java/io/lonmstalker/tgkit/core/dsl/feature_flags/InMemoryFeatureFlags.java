package io.lonmstalker.tgkit.core.dsl.feature_flags;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/** Хранит состояние флагов в памяти. */
public class InMemoryFeatureFlags implements FeatureFlags {
  private final Set<String> disabledFlags = new HashSet<>();
  private final Map<String, Set<Long>> chatFlags = new ConcurrentHashMap<>();
  private final Map<String, Set<Long>> userFlags = new ConcurrentHashMap<>();
  private final Map<String, Integer> percentRoll = new ConcurrentHashMap<>(); // 0..100

  @Override
  public void enableChat(@NonNull String key, long chatId) {
    var flags = chatFlags.get(key);
    if (flags != null) {
      flags.remove(chatId);
    }
  }

  @Override
  public void enableUser(@NonNull String key, long userId) {
    var flags = userFlags.get(key);
    if (flags != null) {
      flags.remove(userId);
    }
  }

  @Override
  public void disableChat(@NonNull String key, long chatId) {
    chatFlags.computeIfAbsent(key, k -> new HashSet<>()).add(chatId);
  }

  @Override
  public void disableUser(@NonNull String key, long userId) {
    userFlags.computeIfAbsent(key, k -> new HashSet<>()).add(userId);
  }

  @Override
  public void rollout(@NonNull String key, int percent) {
    if (percent < 0 || percent > 100) {
      throw new IllegalArgumentException("Invalid percent: " + percent);
    }
    percentRoll.put(key, percent);
  }

  @Override
  public void disable(@NonNull String key) {
    disabledFlags.add(key);
  }

  @Override
  public void enable(@NonNull String key) {
    disabledFlags.remove(key);
  }

  @Override
  public boolean isEnabled(@NonNull String key, long chatId) {
    if (disabledFlags.contains(key) || chatFlags.getOrDefault(key, Set.of()).contains(chatId)) {
      return false;
    }
    var pct = percentRoll.get(key);
    return pct == null || pct >= (chatId % 100);
  }

  @Override
  public boolean isEnabledForUser(@NonNull String key, long userId) {
    if (disabledFlags.contains(key) || userFlags.getOrDefault(key, Set.of()).contains(userId)) {
      return false;
    }
    var pct = percentRoll.get(key);
    return pct == null || pct >= (userId % 100);
  }

  @Override
  public @Nullable Variant variant(@NonNull String key, long entityId) {
    if (disabledFlags.contains(key)) {
      return null;
    }
    Integer pct = percentRoll.get(key);
    if (pct == null) {
      return null;
    }
    return (entityId % 100) < pct ? Variant.VARIANT : Variant.CONTROL;
  }
}
