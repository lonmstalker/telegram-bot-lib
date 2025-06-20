package io.lonmstalker.tgkit.security.secret;

import java.util.Optional;
import java.util.Properties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.checkerframework.checker.nullness.qual.NonNull;

public final class PropertyFileSecretStore implements SecretStore {
  private static final Logger log = LoggerFactory.getLogger(PropertyFileSecretStore.class);
  private volatile Properties props;

  public PropertyFileSecretStore(@NonNull String classPath) {
    this.props = new Properties();
    try (var in = Thread.currentThread().getContextClassLoader().getResourceAsStream(classPath)) {
      if (in != null) {
        props.load(in);
      }
    } catch (Exception e) {
      log.warn("Cannot load secrets.properties", e);
    }
  }

  public PropertyFileSecretStore(@NonNull Properties props) {
    this.props = props;
  }

  @Override
  public Optional<String> get(@NonNull String key) {
    return Optional.ofNullable(props.getProperty(key));
  }
}
