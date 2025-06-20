module io.lonmstalker.tgkit.core {
  requires io.lonmstalker.tgkit.api;
  requires org.slf4j;
  requires org.apache.commons.lang3;
  requires org.telegram.telegrambots;
  requires com.fasterxml.jackson.databind;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires com.h2database;
  requires org.reflections;
  requires io.netty.transport;

  exports io.lonmstalker.tgkit.core.bot;
  exports io.lonmstalker.tgkit.core.config;
  exports io.lonmstalker.tgkit.core.crypto;
  exports io.lonmstalker.tgkit.core.event;
  exports io.lonmstalker.tgkit.core.i18n;
  exports io.lonmstalker.tgkit.core.init;
  exports io.lonmstalker.tgkit.core.loader to
      io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.core.matching;
  exports io.lonmstalker.tgkit.core.processor;
  exports io.lonmstalker.tgkit.core.resource;
  exports io.lonmstalker.tgkit.core.state;
  exports io.lonmstalker.tgkit.core.update;
  exports io.lonmstalker.tgkit.core.user;
  exports io.lonmstalker.tgkit.core.user.store;
  exports io.lonmstalker.tgkit.core.wizard;
  exports io.lonmstalker.tgkit.core.args;
  exports io.lonmstalker.tgkit.core.interceptor;
}
