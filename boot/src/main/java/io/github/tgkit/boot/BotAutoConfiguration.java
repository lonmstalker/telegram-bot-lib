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
package io.github.tgkit.boot;

import io.github.observability.BotObservability;
import io.github.observability.MetricsCollector;
import io.github.observability.impl.NoOpMetricsCollector;
import io.github.tgkit.security.init.BotSecurityInitializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/** Автоконфигурация TgKit для Spring Boot. */
@Configuration(proxyBeanMethods = false)
@EnableConfigurationProperties(BotProperties.class)
public class BotAutoConfiguration {

  @Bean
  TelegramBotRunner telegramBotRunner(BotProperties properties) {
    return new TelegramBotRunner(properties);
  }

  @Bean
  @ConditionalOnClass(BotSecurityInitializer.class)
  InitializingBean securityInitializer() {
    return BotSecurityInitializer::init;
  }

  @Bean
  @ConditionalOnClass(BotObservability.class)
  ObservabilityInitializer observabilityInitializer(BotProperties properties) {
    return new ObservabilityInitializer(properties);
  }

  static final class ObservabilityInitializer implements InitializingBean, DisposableBean {
    private final BotProperties properties;
    private MetricsCollector collector;

    ObservabilityInitializer(BotProperties properties) {
      this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
      collector = BotObservability.micrometer(properties.getMetricsPort());
      io.github.tgkit.internal.config.BotGlobalConfig.INSTANCE.observability().collector(collector);
    }

    @Override
    public void destroy() throws Exception {
      if (collector != null) {
        collector.close();
        io.github.tgkit.internal.config.BotGlobalConfig.INSTANCE
            .observability()
            .collector(new NoOpMetricsCollector());
      }
    }
  }
}
