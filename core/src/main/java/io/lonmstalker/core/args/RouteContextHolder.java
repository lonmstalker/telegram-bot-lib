package io.lonmstalker.core.args;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.regex.Matcher;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RouteContextHolder {
    private static final ThreadLocal<Matcher> MATCHER = new ThreadLocal<>();

    public static void setMatcher(Matcher matcher) {
        MATCHER.set(matcher);
    }

    public static Matcher getMatcher() {
        return MATCHER.get();
    }

    public static void clear() {
        MATCHER.remove();
    }
}
