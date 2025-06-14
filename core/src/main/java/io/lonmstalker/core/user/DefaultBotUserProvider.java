package io.lonmstalker.core.user;

/**
 * Default {@link BotUserProvider} implementation that stores users in a file DB.
 */
public class DefaultBotUserProvider extends DbBotUserProvider {

    public DefaultBotUserProvider() {
        this("./bot-user-db");
    }

    public DefaultBotUserProvider(String dbFile) {
        super(new FileBotUserRepository(dbFile));
    }
}
