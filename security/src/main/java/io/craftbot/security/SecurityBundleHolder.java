package io.craftbot.security;

public final class SecurityBundleHolder {
    private static SecurityInterceptor interceptor;

    private SecurityBundleHolder() {}

    public static void set(SecurityInterceptor i) {
        interceptor = i;
    }

    public static SecurityInterceptor get() {
        return interceptor;
    }
}
