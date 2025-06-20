package io.lonmstalker.tgkit.boot;

import io.lonmstalker.observability.BotObservability;
import io.lonmstalker.observability.MetricsCollector;
import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Автоконфигурация TgKit для Spring Boot.
 */
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

  static final class ObservabilityInitializer
      implements InitializingBean, DisposableBean {
    private final BotProperties properties;
    private MetricsCollector collector;

    ObservabilityInitializer(BotProperties properties) {
      this.properties = properties;
    }

    @Override
    public void afterPropertiesSet() {
      collector = BotObservability.micrometer(properties.getMetricsPort());
    }

    @Override
    public void destroy() throws Exception {
      if (collector != null) {
        collector.close();
      }
    }
  }
}
