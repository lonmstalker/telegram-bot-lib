package io.lonmstalker.tgkit.testkit;

import io.lonmstalker.tgkit.core.bot.BotConfig;
import io.lonmstalker.tgkit.core.bot.TelegramSender;
import io.lonmstalker.tgkit.core.bot.BotAdapterImpl;
import io.lonmstalker.tgkit.core.init.BotCoreInitializer;
import org.junit.jupiter.api.extension.AfterEachCallback;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.api.extension.ParameterContext;
import org.junit.jupiter.api.extension.ParameterResolver;

/**
 * JUnit extension, подготавливающий {@link TelegramMockServer} и утилиты.
 */
public final class BotTestExtension implements BeforeEachCallback, AfterEachCallback, ParameterResolver {

    private TelegramMockServer server;
    private TelegramSender sender;
    private UpdateInjector injector;
    private BotAdapterImpl adapter;

    @Override
    public void beforeEach(ExtensionContext context) {
        BotCoreInitializer.init();
        server = new TelegramMockServer();
        BotConfig config = BotConfig.builder().baseUrl(server.baseUrl()).build();
        sender = new TelegramSender(config, "TEST_TOKEN");
        adapter = BotAdapterImpl.builder()
                .internalId(1L)
                .sender(sender)
                .config(config)
                .build();
        injector = new UpdateInjector(adapter, sender);
        // ответ для getMe
        server.enqueue("{\"ok\":true,\"result\":{\"id\":1,\"is_bot\":true,\"username\":\"bot\"}}");
    }

    @Override
    public void afterEach(ExtensionContext context) {
        try {
            if (sender != null) {
                sender.close();
            }
        } catch (Exception ignored) {
        }
        if (server != null) {
            server.close();
        }
    }

    @Override
    public boolean supportsParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        return type == TelegramMockServer.class || type == UpdateInjector.class || type == BotAdapterImpl.class;
    }

    @Override
    public Object resolveParameter(ParameterContext parameterContext, ExtensionContext extensionContext) {
        Class<?> type = parameterContext.getParameter().getType();
        if (type == TelegramMockServer.class) {
            return server;
        } else if (type == UpdateInjector.class) {
            return injector;
        } else {
            return adapter;
        }
    }
}
