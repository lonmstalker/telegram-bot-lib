package io.lonmstalker.tgkit.boot;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Настройки бота для использования вместе со Spring Boot.
 */
@ConfigurationProperties("tgkit.bot")
public class BotProperties {

  private String token;
  private String baseUrl;
  private List<String> packages = new ArrayList<>();
  private String botGroup;
  private Integer requestsPerSecond;
  private int metricsPort = 9180;

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public String getBaseUrl() {
    return baseUrl;
  }

  public void setBaseUrl(String baseUrl) {
    this.baseUrl = baseUrl;
  }

  public List<String> getPackages() {
    return packages;
  }

  public void setPackages(List<String> packages) {
    this.packages = packages == null ? List.of() : new ArrayList<>(packages);
  }

  public String getBotGroup() {
    return botGroup;
  }

  public void setBotGroup(String botGroup) {
    this.botGroup = botGroup;
  }

  public Integer getRequestsPerSecond() {
    return requestsPerSecond;
  }

  public void setRequestsPerSecond(Integer requestsPerSecond) {
    this.requestsPerSecond = requestsPerSecond;
  }

  public int getMetricsPort() {
    return metricsPort;
  }

  public void setMetricsPort(int metricsPort) {
    this.metricsPort = metricsPort;
  }
}
