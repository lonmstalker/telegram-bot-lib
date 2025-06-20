package io.lonmstalker.tgkit.core.args;

import java.util.regex.Matcher;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RouteContextHolder {
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
