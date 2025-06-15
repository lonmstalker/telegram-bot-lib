package io.lonmstalker.tgkit.core.dsl;

import java.util.Set;

/** Простой контекст для тестов. */
public final class FakeContext extends Context {
    public FakeContext(long chatId, Set<String> roles) {
        super(chatId, roles);
    }
}
