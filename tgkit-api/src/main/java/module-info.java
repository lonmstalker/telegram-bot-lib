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
module io.github.tgkit.api {
  requires transitive telegrambots;
  requires transitive telegrambots.meta;
  requires transitive org.slf4j;
  requires transitive java.net.http;
  requires transitive org.apache.httpcomponents.httpclient;
  requires transitive org.apache.httpcomponents.httpcore;
  requires static webhook;
  requires static com.fasterxml.jackson.annotation;
  requires static org.checkerframework.checker.qual;

  exports io.github.tgkit.api;
  exports io.github.tgkit.api.annotation;
  exports io.github.tgkit.api.args;
  exports io.github.tgkit.api.bot;
  exports io.github.tgkit.api.config;
  exports io.github.tgkit.api.crypto;
  exports io.github.tgkit.api.dsl;
  exports io.github.tgkit.api.dsl.context;
  exports io.github.tgkit.api.dsl.feature_flags;
  exports io.github.tgkit.api.dsl.ttl;
  exports io.github.tgkit.api.dsl.validator;
  exports io.github.tgkit.api.event;
  exports io.github.tgkit.api.exception;
  exports io.github.tgkit.api.i18n;
  exports io.github.tgkit.api.interceptor;
  exports io.github.tgkit.api.matching;
  exports io.github.tgkit.api.parse_mode;
  exports io.github.tgkit.api.resource;
  exports io.github.tgkit.api.state;
  exports io.github.tgkit.api.storage;
  exports io.github.tgkit.api.ttl;
  exports io.github.tgkit.api.user;
  exports io.github.tgkit.api.user.store;
  exports io.github.tgkit.api.wizard;
  exports io.github.tgkit.api.validator;
  exports io.github.tgkit.api.annotation.wizard;
  exports io.github.tgkit.api.observability;
  exports io.github.tgkit.api.plugin;
  exports io.github.tgkit.api.security.antispam;
  exports io.github.tgkit.api.security.audit;
  exports io.github.tgkit.api.security.captcha;
  exports io.github.tgkit.api.security.ratelimit;
  exports io.github.tgkit.api.security.rbac;
  exports io.github.tgkit.api.security.secret;
}
