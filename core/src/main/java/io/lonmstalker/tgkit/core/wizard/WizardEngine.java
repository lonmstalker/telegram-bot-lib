package io.lonmstalker.tgkit.core.wizard;

import io.lonmstalker.tgkit.core.BotRequest;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Диспетчер пошаговых сценариев (wizard). Поддерживает мультисессии, pause/resume,
 * timeout/reminders и ветвления.
 */
public interface WizardEngine {

  /**
   * Регистрирует новый сценарий.
   *
   * @param wizard экземпляр, аннотированный @WizardMeta
   */
  void register(@NonNull Wizard<?> wizard);

  /**
   * Запускает сценарий, создаёт новую сессию и возвращает её sessionId.
   *
   * @param wizardId идентификатор сценария
   * @param request входящее сообщение
   * @return уникальный sessionId
   */
  @NonNull String start(@NonNull String wizardId, @NonNull BotRequest<?> request);

  /**
   * Продолжает работу существующей или создаёт новую сессию, исходя из данных в request (payload
   * содержит sessionId).
   *
   * @param request входящее сообщение (или callback), содержит sessionId
   */
  void route(@NonNull BotRequest<?> request);

  /**
   * Возобновляет ранее приостановленный сценарий (например, по команде /resume).
   *
   * @param request входящее сообщение
   */
  void resume(@NonNull BotRequest<?> request);
}
