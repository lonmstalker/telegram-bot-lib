package io.lonmstalker.tgkit.plugin;

public enum BotPluginPermission {
    READ_UPDATES(1L),
    SEND_MESSAGES(1L << 1),
    EDIT_MESSAGES(1L << 2),
    DELETE_MESSAGES(1L << 3),
    SCHEDULE_TASKS(1L << 4),
    NETWORK_IO(1L << 5);

    public final long mask;
    BotPluginPermission(long mask) { this.mask = mask; }
}
