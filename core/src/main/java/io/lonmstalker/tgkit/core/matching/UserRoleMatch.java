package io.lonmstalker.tgkit.core.matching;

import io.lonmstalker.tgkit.core.storage.BotRequestHolder;
import io.lonmstalker.tgkit.core.user.BotUserInfo;
import io.lonmstalker.tgkit.core.user.BotUserProvider;
import java.util.Set;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Matcher that checks user roles using {@link BotUserProvider}.
 */
public class UserRoleMatch<T extends BotApiObject> implements CommandMatch<T> {

    private final BotUserProvider provider;
    private final Set<String> roles;

    public UserRoleMatch(@NonNull BotUserProvider provider, @NonNull Set<String> roles) {
        this.provider = provider;
        this.roles = Set.copyOf(roles);
    }

    @Override
    public boolean match(@NonNull T data) {
        Update update = BotRequestHolder.getUpdate();
        if (update == null) {
            return false;
        }
        BotUserInfo user = provider.resolve(update);
        return user.roles().stream().anyMatch(roles::contains);
    }
}
