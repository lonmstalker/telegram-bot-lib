package io.lonmstalker.tgkit.boot;

import static org.assertj.core.api.Assertions.assertThat;

import io.lonmstalker.tgkit.security.init.BotSecurityInitializer;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.FilteredClassLoader;

class BotAutoConfigurationTest {

  private final ApplicationContextRunner runner =
      new ApplicationContextRunner()
          .withConfiguration(AutoConfigurations.of(BotAutoConfiguration.class))
          .withPropertyValues(
              "tgkit.bot.token=T",
              "tgkit.bot.base-url=http://localhost",
              "tgkit.bot.packages=io.test");

  @Test
  void createsRunnerBean() {
    runner.run(ctx -> assertThat(ctx).hasSingleBean(TelegramBotRunner.class));
  }

  @Test
  void securityConditional() {
    runner
        .withClassLoader(new FilteredClassLoader(BotSecurityInitializer.class))
        .run(context -> assertThat(context.containsBean("securityInitializer")).isFalse());
  }
}
