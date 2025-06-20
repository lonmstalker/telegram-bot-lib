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
package io.github.tgkit.api;

import io.github.tgkit.api.bot.TelegramSender;
import io.github.tgkit.api.i18n.MessageLocalizer;
import io.github.tgkit.api.state.StateStore;
import io.github.tgkit.api.user.store.UserKVStore;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Сервисы для работы команд
 *
 * @param store хранилище пользовательского состояния
 * @param sender объект для отправки сообщений Telegram
 * @param userKVStore объект для хранения дополнительной информации пользователя
 * @param localizer сервис локализации сообщений
 */
public record BotService(
    @NonNull StateStore store,
    @NonNull TelegramSender sender,
    @NonNull UserKVStore userKVStore,
    @NonNull MessageLocalizer localizer) {}
