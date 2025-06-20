package io.lonmstalker.tgkit.plugin;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;
import lombok.Builder;

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

  @Builder
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
}
