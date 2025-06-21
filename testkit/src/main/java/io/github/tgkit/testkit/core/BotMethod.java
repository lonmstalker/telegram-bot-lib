package io.github.tgkit.testkit.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;

/** Обертка над {@link BotApiMethod}, используемая в тестовой транспортной системе. */
public record BotMethod<T>(@NonNull BotApiMethod<T> method) {}
