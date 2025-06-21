# 📦 flag-test
*Тестовые утилиты для override фич-флагов*

`flag-test` предоставляет `FlagOverrideRegistry` и `FlagOverrideExtension`,\
которые позволяют временно изменять значения фич-флагов в тестах и
собирать простую статистику покрытия веток.

## Подключение

```xml
<dependency>
    <groupId>io.github.tgkit</groupId>
    <artifactId>flag-test</artifactId>
    <version>0.0.1-SNAPSHOT</version>
    <scope>test</scope>
</dependency>
```

## Мини-пример

```java
import static org.junit.jupiter.api.Assertions.assertTrue;
import io.github.tgkit.flag.test.FlagOverrideExtension;
import io.github.tgkit.flag.test.Flags;
import io.github.tgkit.internal.config.BotGlobalConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(FlagOverrideExtension.class)
class ExampleTest {
  @Test
  void overrideFlag(Flags flags) {
    flags.enable("NEW_FEATURE");
    boolean enabled = BotGlobalConfig.INSTANCE.dsl()
        .getFeatureFlags().isEnabled("NEW_FEATURE", 42L);
    assertTrue(enabled);
  }
}
```
