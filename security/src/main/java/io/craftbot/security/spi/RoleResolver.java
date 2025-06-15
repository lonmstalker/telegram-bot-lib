package io.craftbot.security.spi;

public interface RoleResolver {
    boolean hasRole(long userId, String role);
}
