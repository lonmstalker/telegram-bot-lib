/*
 * Copyright (C) 2024 the original author or authors.
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
module io.lonmstalker.tgkit.core {
  requires io.lonmstalker.tgkit.api;
  requires org.slf4j;
  requires org.apache.commons.lang3;
  requires telegrambots;
  requires telegrambots.meta;
  requires static org.checkerframework.checker.qual;
  requires static com.fasterxml.jackson.annotation;
  requires transitive com.fasterxml.jackson.databind;
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
