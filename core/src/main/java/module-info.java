/*
 * Copyright 2025 TgKit Team
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
module io.github.tgkit.core {
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
  requires jedis;
  requires io.netty.transport;

  exports io.github.tgkit.core.bot;
  exports io.github.tgkit.core;
  exports io.github.tgkit.core.config;
  exports io.github.tgkit.core.crypto;
  exports io.github.tgkit.core.event;
  exports io.github.tgkit.core.i18n;
  exports io.github.tgkit.core.init;
  exports io.github.tgkit.core.loader to
      io.lonmstalker.tgkit.plugin;
  exports io.github.tgkit.core.matching;
  exports io.github.tgkit.core.processor;
  exports io.github.tgkit.core.resource;
  exports io.github.tgkit.core.state;
  exports io.github.tgkit.core.update;
  exports io.github.tgkit.core.user;
  exports io.github.tgkit.core.user.store;
  exports io.github.tgkit.core.wizard;
  exports io.github.tgkit.core.args;
  exports io.github.tgkit.core.interceptor;
}
