package io.lonmstalker.tgkit.core.args;

import java.util.regex.Matcher;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class RouteContextHolder {
  private RouteContextHolder() {}
  private static final ThreadLocal<@Nullable Matcher> MATCHER = new ThreadLocal<>();

  public static void setMatcher(@NonNull Matcher matcher) {
    MATCHER.set(matcher);
  }

  public static @Nullable Matcher getMatcher() {
    return MATCHER.get();
  }

  public static void clear() {
    MATCHER.remove();
  }
}
