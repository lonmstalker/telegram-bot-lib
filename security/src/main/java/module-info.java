module io.lonmstalker.tgkit.security {
    requires io.lonmstalker.tgkit.core;
    requires org.slf4j;
    requires com.github.benmanes.caffeine;
    requires io.github.jopenlibs.vault.java.driver;
    requires jedis;

    exports io.lonmstalker.tgkit.security;
    exports io.lonmstalker.tgkit.security.antispam;
    exports io.lonmstalker.tgkit.security.audit;
    exports io.lonmstalker.tgkit.security.captcha;
    exports io.lonmstalker.tgkit.security.captcha.provider;
    exports io.lonmstalker.tgkit.security.event;
    exports io.lonmstalker.tgkit.security.init;
    exports io.lonmstalker.tgkit.security.ratelimit;
    exports io.lonmstalker.tgkit.security.ratelimit.impl to io.lonmstalker.tgkit.plugin;
    exports io.lonmstalker.tgkit.security.rbac;
    exports io.lonmstalker.tgkit.security.secret;
}
