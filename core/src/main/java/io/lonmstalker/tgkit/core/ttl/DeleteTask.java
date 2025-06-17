package io.lonmstalker.tgkit.core.ttl;

import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Задача удаления.
 */
public record DeleteTask(long chatId,
                         long messageId,
                         @NonNull Runnable action) {
}