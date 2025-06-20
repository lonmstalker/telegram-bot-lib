package io.lonmstalker.tgkit.doc.mapper;

/**
 * Внутреннее представление метода Telegram API.
 *
 * @param name        имя метода
 * @param description краткое описание
 */
public record OperationInfo(String name, String description) {}
