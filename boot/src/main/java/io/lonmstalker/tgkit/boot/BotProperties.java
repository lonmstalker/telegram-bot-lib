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
package io.lonmstalker.tgkit.boot;

import java.util.ArrayList;
import java.util.List;
import org.springframework.boot.context.properties.ConfigurationProperties;

/** Настройки бота для использования вместе со Spring Boot. */
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
