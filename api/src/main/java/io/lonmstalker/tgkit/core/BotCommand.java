package io.lonmstalker.tgkit.core;

import io.lonmstalker.tgkit.core.interceptor.BotInterceptor;
import io.lonmstalker.tgkit.core.matching.CommandMatch;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * Обработчик конкретной команды бота.
 *
 * <p><b>Стабильность:</b> API считается стабильным и совместимым между версиями.
 *
 * @param <T> тип объекта Telegram API, с которым работает обработчик
 */
public interface BotCommand<T> {

  /**
   * Выполняет обработку запроса пользователя.
   *
   * @param request запрос, содержащий данные об обновлении
   * @return {@link BotResponse}, который необходимо отправить пользователю, либо {@code null}, если
   *     ответ не требуется
   */
  @Nullable BotResponse handle(@NonNull BotRequest<T> request);

  /**
   * Тип обрабатываемого запроса.
   *
   * @return тип запроса
   */
  @NonNull BotRequestType type();

  /**
   * Правило сопоставления команды с обновлением.
   *
   * @return правило (matcher)
   */
  @NonNull CommandMatch<T> matcher();

  /**
   * Список интерсепторов команды.
   *
   * @return изменяемый список
   */
  @NonNull List<BotInterceptor> interceptors();

  void setMatcher(@NonNull CommandMatch<T> matcher);

  void setType(@NonNull BotRequestType type);

  void setBotGroup(@NonNull String group);

  /**
   * Группа обработчика. Используется для объединения команд.
   *
   * @return название группы
   */
  default @NonNull String botGroup() {
    return "";
  }

  /**
   * Порядок выполнения команды (меньше — выше приоритет).
   *
   * @return целочисленный порядок
   */
  default int order() {
    return BotCommandOrder.LAST;
  }

  /**
   * Добавляет {@link BotInterceptor} к данной команде.
   *
   * @param interceptor интерсептор, выполняющийся до/после handle()
   */
  default void addInterceptor(@NonNull BotInterceptor interceptor) {
    interceptors().add(interceptor);
  }

  /**
   * Краткое описание команды для help-системы.
   *
   * @return текст описания
   */
  default @NonNull String getDescription() {
    return "";
  }

  /**
   * Пример использования команды.
   *
   * @return текст-образец
   */
  default @NonNull String getUsage() {
    return "";
  }
}
