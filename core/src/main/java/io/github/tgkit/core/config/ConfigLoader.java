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
package io.github.tgkit.core.config;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.tgkit.core.resource.Loaders;
import io.github.tgkit.core.resource.ResourceLoader;
import java.io.IOException;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

/**
 * Утилита для загрузки конфигураций YAML/JSON и маппинга их на POJO-классы.
 *
 * <p>Пример:
 *
 * <pre>{@code
 * ConfigLoader loader = new ConfigLoader();
 * MyCfg cfg = loader.as("config.yml", MyCfg.class);
 * }</pre>
 */
public class ConfigLoader {

  private static final ObjectMapper YAML = new ObjectMapper(new YAMLFactory());
  private static final ObjectMapper JSON = new ObjectMapper();

  /*------------------- helpers -------------------*/
  private static JsonNode parse(InputStream in, String id) throws IOException {
    if (id.endsWith(".yml") || id.endsWith(".yaml")) {
      return YAML.readTree(in);
    }
    if (id.endsWith(".json")) {
      return JSON.readTree(in);
    }
    throw new IOException("unsupported config format: " + id);
  }

  /** Читает и мержит YAML+JSON в единое JsonNode */
  public @NonNull JsonNode asJsonNode(@NonNull String path) throws IOException {
    JsonNode merged = JSON.nullNode();
    ResourceLoader loader = Loaders.load(path);
    try (InputStream is = loader.open()) {
      return parse(is, loader.text());
    }
  }

  /** Приводит конфиг к строго типизированному POJO */
  public <T> T as(@NonNull String path, @NonNull Class<T> type) throws IOException {
    return JSON.treeToValue(asJsonNode(path), type);
  }
}
