package io.lonmstalker.tgkit.plugin;

import lombok.Getter;
import lombok.Setter;

/**
 * Разрешения плагина. Влияют на доступ к чувствительным операциям ядра.
 */
@Getter
@Setter
public class PluginPermissions {

    /** Доступ к сети. */
    public enum Network { NONE, OUTBOUND, FULL }

    /** Доступ к хранилищу. */
    public enum Store { NONE, READ_ONLY, READ_WRITE }

    /** Доступ к файловой системе. */
    public enum FileSystem { NONE, READ, WRITE, READ_WRITE }

    private Network network = Network.NONE;
    private Store store = Store.NONE;
    private FileSystem fileSystem = FileSystem.NONE;
    private boolean botControl = false;
}
