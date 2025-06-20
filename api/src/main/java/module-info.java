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
module io.lonmstalker.tgkit.api {
  requires transitive telegrambots;
  requires transitive telegrambots.meta;
  requires transitive org.slf4j;
  requires transitive java.net.http;
  requires transitive org.apache.httpcomponents.httpclient;
  requires transitive org.apache.httpcomponents.httpcore;
  requires static com.fasterxml.jackson.annotation;
  requires static org.checkerframework.checker.qual;

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
  exports io.lonmstalker.tgkit.core.validator;
  exports io.lonmstalker.tgkit.core.annotation.wizard;
  exports io.lonmstalker.tgkit.observability;
  exports io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.security.antispam;
  exports io.lonmstalker.tgkit.security.audit;
  exports io.lonmstalker.tgkit.security.captcha;
  exports io.lonmstalker.tgkit.security.ratelimit;
  exports io.lonmstalker.tgkit.security.rbac;
  exports io.lonmstalker.tgkit.security.secret;
}
