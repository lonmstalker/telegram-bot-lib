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
package io.github.tgkit.security.event;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.bot.BotRegistryImpl;
import io.github.tgkit.core.event.TelegramBotEvent;
import io.github.tgkit.core.exception.BotApiException;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.NonNull;

public record SecurityBotEvent(
    @NonNull Type type, @NonNull Instant timestamp, @NonNull BotRequest<?> request)
    implements TelegramBotEvent {

  @Override
  public long botInternalId() {
    return request.botInfo().internalId();
  }

  @Override
  public long botExternalId() {
    return BotRegistryImpl.INSTANCE
        .getByInternalId(botInternalId())
        .orElseThrow(() -> new BotApiException("Cannot find bot"))
        .externalId();
  }

  public enum Type {
    FLOOD,
    DUPLICATE,
    MALICIOUS_URL
  }
}
