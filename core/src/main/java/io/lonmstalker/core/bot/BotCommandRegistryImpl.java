package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotCommandRegistryImpl implements BotCommandRegistry {
    private static final Logger log = LoggerFactory.getLogger(BotCommandRegistryImpl.class);
    private final Map<BotRequestType, List<BotCommand<?>>> commands = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BotApiObject> @Nullable BotCommand<T> find(@NonNull BotRequestType type, @NonNull T data) {
        type.checkType(data.getClass());
        var commands = this.commands.get(type);

        if (commands == null) {
            return null;
        }

        for (BotCommand<?> cmd : commands) {
            CommandMatch<T> matcher = (CommandMatch<T>) cmd.matcher();
            if (matcher.match(data)) {
                return (BotCommand<T>) cmd;
            } else {
                log.debug("Command {} skipped: {} returned false", cmd.getClass().getSimpleName(), matcher.getClass().getSimpleName());
            }
        }
        return null;
    }

    @Override
    public void add(@NonNull BotCommand<?> command) {
        commands.compute(command.type(), (__, v) -> {
            if (v == null) {
                v = new CopyOnWriteArrayList<>();
                v.add(command);
            } else {
                v.add(command);
                v.sort(Comparator.comparing(BotCommand::order));
            }
            return v;
        });
    }
}
