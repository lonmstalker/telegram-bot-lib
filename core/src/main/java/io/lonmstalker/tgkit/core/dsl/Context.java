package io.lonmstalker.tgkit.core.dsl;

import java.util.Set;

/**
 * Контекст выполнения ответа.
 *
 * @param chatId идентификатор чата
 * @param roles  роли пользователя
 */
public record Context(long chatId, Set<String> roles) {
    /** Проверяет роль администратора. */
    public boolean isAdmin() {
        return roles.contains("ADMIN");
    }
}
