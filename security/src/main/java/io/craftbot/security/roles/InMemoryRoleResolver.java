package io.craftbot.security.roles;

import io.craftbot.security.spi.RoleResolver;

import java.util.Map;

public class InMemoryRoleResolver implements RoleResolver {
    private final Map<Long, String> roles;

    public InMemoryRoleResolver(Map<Long, String> roles) {
        this.roles = roles;
    }

    @Override
    public boolean hasRole(long userId, String role) {
        return role.equals(roles.get(userId));
    }
}
