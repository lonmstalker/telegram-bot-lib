package io.lonmstalker.core.bot;

import io.lonmstalker.core.BotCommand;
import io.lonmstalker.core.BotRequestType;
import io.lonmstalker.core.matching.CommandMatch;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BotCommandRegistryImpl implements BotCommandRegistry {
    private final Map<BotRequestType, List<BotCommand<?>>> commands = new ConcurrentHashMap<>();

    @Override
    @SuppressWarnings("unchecked")
    public <T extends BotApiObject> @Nullable BotCommand<T> find(@NonNull BotRequestType type, @NonNull T data) {
        type.checkType(data.getClass());
        var commands = this.commands.get(type);

        if (commands == null) {
            return null;
        }

        return (BotCommand<T>) commands.stream()
                .filter(command -> ((CommandMatch<T>) command.matcher()).match(data))
                .findFirst()
                .orElse(null);
    }

    @Override
    public void add(@NonNull BotCommand<?> command) {
        commands.compute(command.type(), (__, v) -> {
            if (v == null) {
                v = new ArrayList<>();
                v.add(command);
            } else {
                v.add(command);
                v.sort(Comparator.comparing(BotCommand::order));
            }
            return v;
        });
    }
}
