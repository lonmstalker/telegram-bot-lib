package io.lonmstalker.tgkit.plugin;

import io.lonmstalker.tgkit.core.exception.BotApiException;

public class PluginException extends BotApiException {

  public PluginException(String message) {
    super(message);
  }

  public PluginException(String message, Throwable cause) {
    super(message, cause);
  }

  public PluginException(Throwable cause) {
    super(cause);
  }
}
