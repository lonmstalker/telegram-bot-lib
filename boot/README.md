# 📦 tgkit-boot
*Spring Boot интеграция для **tgkit***

---

## Подключение

```xml
<dependency>
    <groupId>io.lonmstalker.tgkit</groupId>
    <artifactId>boot</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```

После добавления зависимости создайте файл `application.yml`:

```yaml
tgkit:
  bot:
    token: "TOKEN"
    packages:
      - com.example.bot
```

И просто пометьте класс как `@SpringBootApplication` — бот запустится вместе с приложением.
