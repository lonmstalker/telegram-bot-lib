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
package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.annotation.Arg;
import java.lang.reflect.Parameter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Метаданные одного параметра метода:
 *
 * <ul>
 *   <li>{@code order} – порядок переменной в методе;
 *   <li>{@code request} – если {@link BotRequest};
 *   <li>{@code update} – если {@link Update};
 *   <li>{@code arg} – аннотация {@link Arg} /default;
 *   <li>{@code parameter} – параметр в методе {@link Parameter} /default;
 *   <li>{@code converter} – конвертер строки в нужный тип параметра.
 * </ul>
 */
public record ParamInfo(
    int order,
    boolean request,
    boolean update,
    @Nullable Arg arg,
    @NonNull Parameter parameter,
    @NonNull BotArgumentConverter<Object, Object> converter) {}
