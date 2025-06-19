package io.lonmstalker.tgkit.core.matching;

import org.checkerframework.checker.nullness.qual.NonNull;

@FunctionalInterface
@SuppressWarnings({"unchecked", "rawtypes"})
public interface CommandMatch<T> {

    boolean match(@NonNull T data);

    default CommandMatch<T> and(@NonNull CommandMatch... other) {
        return new CommandMatchAnd<>(other);
    }

    default CommandMatch<T> or(@NonNull CommandMatch... other) {
        return new CommandMatchOr<>(other);
    }

    class CommandMatchOr<T> implements CommandMatch<T> {
        private final CommandMatch<T>[] commandMatchers;

        @SafeVarargs
        public CommandMatchOr(@NonNull CommandMatch<T>... commandMatchers) {
            this.commandMatchers = commandMatchers;
        }


        @Override
        public boolean match(@NonNull T data) {
            for (CommandMatch<T> matcher : commandMatchers) {
                if (matcher.match(data)) {
                    return true;
                }
            }
            return false;
        }
    }

    class CommandMatchAnd<T> implements CommandMatch<T> {
        private final CommandMatch<T>[] commandMatchers;

        @SafeVarargs
        public CommandMatchAnd(@NonNull CommandMatch<T>... commandMatchers) {
            this.commandMatchers = commandMatchers;
        }


        @Override
        public boolean match(@NonNull T data) {
            for (CommandMatch<T> matcher : commandMatchers) {
                if (!matcher.match(data)) {
                    return false;
                }
            }
            return true;
        }
    }
}
