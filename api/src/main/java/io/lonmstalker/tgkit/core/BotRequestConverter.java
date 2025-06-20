package io.lonmstalker.tgkit.core;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

/**
 * Преобразователь {@link Update} в необходимый тип данных запроса.
 *
 * <p><b>Стабильность:</b> API находится в стадии эксперимента и может изменяться.
 *
 * @param <T> тип результата преобразования
 */
@FunctionalInterface
public interface BotRequestConverter<T> {

  /**
   * Конвертирует обновление Telegram в нужный тип.
   *
   * @param update объект {@link Update}, полученный от Telegram
   * @param type тип запроса, определённый библиотекой
   * @return сконвертированный объект
   */
  @NonNull T convert(@NonNull Update update, @NonNull BotRequestType type);
}
