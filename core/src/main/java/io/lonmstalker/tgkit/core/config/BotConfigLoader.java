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

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.checkerframework.checker.nullness.qual.NonNull;

/** Утилита для чтения конфигурации бота из YAML или JSON. */
public final class BotConfigLoader {

  private static final ObjectMapper YAML_MAPPER = new ObjectMapper(new YAMLFactory());
  private static final ObjectMapper JSON_MAPPER = new ObjectMapper();

  private BotConfigLoader() {}

  /**
   * Загружает настройки из файла YAML или JSON.
   *
   * @param path путь к конфигурации
   * @return заполненные параметры
   * @throws IOException при ошибке чтения файла
   */
  public static @NonNull Settings load(@NonNull Path path) throws IOException {
    ObjectMapper mapper =
        path.toString().endsWith(".yml") || path.toString().endsWith(".yaml")
            ? YAML_MAPPER
            : JSON_MAPPER;
    try (InputStream in = Files.newInputStream(path)) {
      return mapper.readValue(in, Settings.class);
    }
  }

  /**
   * Минимальная схема конфигурации бота.
   *
   * <p>Пример YAML:
   *
   * <pre>{@code
   * token: "123"
   * base-url: "http://localhost:8080/bot"
   * bot-group: "demo"
   * requests-per-second: 25
   * packages:
   *   - io.example.bot
   * }</pre>
   */
  @JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
  public static final class Settings {
    private final String token;
    private final String baseUrl;
    private final List<String> packages;
    private final String botGroup;
    private final Integer requestsPerSecond;

    @JsonCreator
    public Settings(
        @JsonProperty(value = "token", required = true) String token,
        @JsonProperty("base-url") String baseUrl,
        @JsonProperty("packages") List<String> packages,
        @JsonProperty("bot-group") String botGroup,
        @JsonProperty("requests-per-second") Integer requestsPerSecond) {
      this.token = token;
      this.baseUrl = baseUrl;
      this.packages = packages == null ? List.of() : List.copyOf(packages);
      this.botGroup = botGroup;
      this.requestsPerSecond = requestsPerSecond;
    }

    public String token() {
      return token;
    }

    public String baseUrl() {
      return baseUrl;
    }

    public List<String> packages() {
      return packages;
    }

    public String botGroup() {
      return botGroup;
    }

    public Integer requestsPerSecond() {
      return requestsPerSecond;
    }
  }
}
