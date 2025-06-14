package io.lonmstalker.core.matching;

import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Combines several matchers using logical OR.
 */
public class OrMatch<T extends BotApiObject> implements CommandMatch<T> {
    private static final Logger log = LoggerFactory.getLogger(OrMatch.class);
    private final List<CommandMatch<T>> matchers;

    public OrMatch(@NonNull List<CommandMatch<T>> matchers) {
        this.matchers = List.copyOf(matchers);
    }

    @SafeVarargs
    public OrMatch(@NonNull CommandMatch<T>... matchers) {
        this.matchers = List.of(matchers);
    }

    @Override
    public boolean match(@NonNull T data) {
        for (CommandMatch<T> m : matchers) {
            if (m.match(data)) {
                return true;
            } else {
                log.debug("OrMatch: {} failed", m.getClass().getSimpleName());
            }
        }
        return false;
    }
}
