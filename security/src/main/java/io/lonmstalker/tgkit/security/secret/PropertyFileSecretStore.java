/*
 * Copyright 2025 TgKit Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package io.lonmstalker.tgkit.security.secret;

import java.util.Optional;
import java.util.Properties;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
