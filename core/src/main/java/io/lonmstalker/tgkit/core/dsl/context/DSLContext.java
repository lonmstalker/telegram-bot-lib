package io.lonmstalker.tgkit.core.dsl.context;

import io.lonmstalker.tgkit.core.BotInfo;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Контекст выполнения ответа.
 */
public interface DSLContext {

    @NonNull
    BotInfo botInfo();

    @NonNull
    BotUserInfo userInfo();

    /**
     * Проверяет роль администратора.
     */
    boolean isAdmin();

    record SimpleDSLContext(@NonNull BotInfo botInfo,
                            @NonNull BotUserInfo userInfo) implements DSLContext {

        public SimpleDSLContext {
            Long cId = userInfo.chatId();
            Long uId = userInfo.userId();
            if (cId == null && uId == null) {
                throw new BotApiException("Both chatId and userId are null in update");
            }
        }

        /**
         * Проверяет роль администратора.
         */
        public boolean isAdmin() {
            return userInfo.roles().contains("ADMIN");
        }
    }
}
