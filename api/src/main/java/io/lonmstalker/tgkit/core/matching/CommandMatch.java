/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
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
