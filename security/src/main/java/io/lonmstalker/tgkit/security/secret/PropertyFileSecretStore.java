package io.lonmstalker.tgkit.security.secret;

import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Optional;
import java.util.Properties;

@Slf4j
public final class PropertyFileSecretStore implements SecretStore {
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
