package io.lonmstalker.tgkit.core;


/** Константы, определяющие порядок выполнения обработчиков команд. */
public final class BotCommandOrder {
  private BotCommandOrder() {}

  /** Выполняется первым. */
  public static final int FIRST = Integer.MIN_VALUE;

  /** Выполняется последним. */
  public static final int LAST = Integer.MAX_VALUE;
}
