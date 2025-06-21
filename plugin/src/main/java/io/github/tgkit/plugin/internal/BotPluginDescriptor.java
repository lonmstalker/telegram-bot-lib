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
package io.github.tgkit.plugin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

/** Описатель плагина, маппится из plugin.yml и обратно. */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public final class BotPluginDescriptor {
  @JsonProperty("id")
  private final String id;

  @JsonProperty("name")
  private final String name;

  @JsonProperty("version")
  private final String version;

  @JsonProperty("api")
  private final String api;

  @JsonProperty("mainClass")
  private final String mainClass;

  @JsonProperty("author")
  private final String author;

  @JsonProperty("description")
  private final String description;

  @JsonProperty("license")
  private final String license;

  @JsonProperty("minCoreVersion")
  private final String minCoreVersion;

  @JsonProperty("requires")
  private final List<String> requires;

  @JsonProperty("sha256")
  private final String sha256;

  @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
  public BotPluginDescriptor(
      @JsonProperty("id") String id,
      @JsonProperty("name") String name,
      @JsonProperty("version") String version,
      @JsonProperty("api") String api,
      @JsonProperty("mainClass") String mainClass,
      @JsonProperty("author") String author,
      @JsonProperty("description") String description,
      @JsonProperty("license") String license,
      @JsonProperty("minCoreVersion") String minCoreVersion,
      @JsonProperty("requires") List<String> requires,
      @JsonProperty("sha256") String sha256) {
    this.id = id;
    this.name = name;
    this.version = version;
    this.api = api;
    this.mainClass = mainClass;
    this.author = author;
    this.description = description;
    this.license = license;
    this.minCoreVersion = minCoreVersion;
    this.requires = requires != null ? requires : List.of();
    this.sha256 = sha256;
  }

  public static Builder builder() {
    return new Builder();
  }

  public String id() {
    return id;
  }

  public String name() {
    return name;
  }

  public String version() {
    return version;
  }

  public String api() {
    return api;
  }

  public String mainClass() {
    return mainClass;
  }

  public String author() {
    return author;
  }

  public String description() {
    return description;
  }

  public String license() {
    return license;
  }

  public String minCoreVersion() {
    return minCoreVersion;
  }

  public List<String> requires() {
    return requires;
  }

  public String sha256() {
    return sha256;
  }

  public static final class Builder {
    private String id;
    private String name;
    private String version;
    private String api;
    private String mainClass;
    private String author;
    private String description;
    private String license;
    private String minCoreVersion;
    private List<String> requires;
    private String sha256;

    public Builder id(String id) {
      this.id = id;
      return this;
    }

    public Builder name(String name) {
      this.name = name;
      return this;
    }

    public Builder version(String version) {
      this.version = version;
      return this;
    }

    public Builder api(String api) {
      this.api = api;
      return this;
    }

    public Builder mainClass(String mainClass) {
      this.mainClass = mainClass;
      return this;
    }

    public Builder author(String author) {
      this.author = author;
      return this;
    }

    public Builder description(String description) {
      this.description = description;
      return this;
    }

    public Builder license(String license) {
      this.license = license;
      return this;
    }

    public Builder minCoreVersion(String minCoreVersion) {
      this.minCoreVersion = minCoreVersion;
      return this;
    }

    public Builder requires(List<String> requires) {
      this.requires = requires;
      return this;
    }

    public Builder sha256(String sha256) {
      this.sha256 = sha256;
      return this;
    }

    public BotPluginDescriptor build() {
      return new BotPluginDescriptor(
          id,
          name,
          version,
          api,
          mainClass,
          author,
          description,
          license,
          minCoreVersion,
          requires,
          sha256);
    }
  }
}
