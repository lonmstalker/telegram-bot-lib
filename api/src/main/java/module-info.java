module io.lonmstalker.tgkit.api {
  requires transitive org.telegram.telegrambots;

  exports io.lonmstalker.tgkit.core;
  exports io.lonmstalker.tgkit.core.annotation;
  exports io.lonmstalker.tgkit.core.args;
  exports io.lonmstalker.tgkit.core.bot;
  exports io.lonmstalker.tgkit.core.config;
  exports io.lonmstalker.tgkit.core.crypto;
  exports io.lonmstalker.tgkit.core.dsl;
  exports io.lonmstalker.tgkit.core.dsl.context;
  exports io.lonmstalker.tgkit.core.dsl.feature_flags;
  exports io.lonmstalker.tgkit.core.dsl.ttl;
  exports io.lonmstalker.tgkit.core.dsl.validator;
  exports io.lonmstalker.tgkit.core.event;
  exports io.lonmstalker.tgkit.core.exception;
  exports io.lonmstalker.tgkit.core.i18n;
  exports io.lonmstalker.tgkit.core.interceptor;
  exports io.lonmstalker.tgkit.core.matching;
  exports io.lonmstalker.tgkit.core.parse_mode;
  exports io.lonmstalker.tgkit.core.resource;
  exports io.lonmstalker.tgkit.core.state;
  exports io.lonmstalker.tgkit.core.storage;
  exports io.lonmstalker.tgkit.core.ttl;
  exports io.lonmstalker.tgkit.core.user;
  exports io.lonmstalker.tgkit.core.user.store;
  exports io.lonmstalker.tgkit.core.wizard;
  exports io.lonmstalker.tgkit.core.wizard.annotation;
  exports io.lonmstalker.tgkit.observability;
  exports io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.security.antispam;
  exports io.lonmstalker.tgkit.security.audit;
  exports io.lonmstalker.tgkit.security.captcha;
  exports io.lonmstalker.tgkit.security.ratelimit;
  exports io.lonmstalker.tgkit.security.rbac;
  exports io.lonmstalker.tgkit.security.secret;
}
