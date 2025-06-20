/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.event.impl;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.event.BotEvent;
import java.lang.reflect.Method;
import java.time.Instant;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.interfaces.BotApiObject;

public record RegisterCommandBotEvent(
    @NonNull Instant timestamp,
    @Nullable Method method,
    @NonNull BotCommand<? extends BotApiObject> command)
    implements BotEvent {}
