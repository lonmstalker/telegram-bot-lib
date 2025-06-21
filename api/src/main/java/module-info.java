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

  exports io.github.tgkit.internal;
  exports io.github.tgkit.internal.annotation;
  exports io.github.tgkit.internal.args;
  exports io.github.tgkit.internal.bot;
  exports io.github.tgkit.internal.config;
  exports io.github.tgkit.internal.crypto;
  exports io.github.tgkit.internal.dsl;
  exports io.github.tgkit.internal.dsl.context;
  exports io.github.tgkit.internal.dsl.feature_flags;
  exports io.github.tgkit.internal.dsl.ttl;
  exports io.github.tgkit.internal.dsl.validator;
  exports io.github.tgkit.internal.event;
  exports io.github.tgkit.internal.exception;
  exports io.github.tgkit.internal.i18n;
  exports io.github.tgkit.internal.interceptor;
  exports io.github.tgkit.internal.matching;
  exports io.github.tgkit.internal.parse_mode;
  exports io.github.tgkit.internal.resource;
  exports io.github.tgkit.internal.state;
  exports io.github.tgkit.internal.storage;
  exports io.github.tgkit.internal.ttl;
  exports io.github.tgkit.internal.user;
  exports io.github.tgkit.internal.user.store;
  exports io.github.tgkit.internal.wizard;
  exports io.github.tgkit.internal.validator;
  exports io.github.tgkit.internal.annotation.wizard;
  exports io.github.tgkit.observability;
  exports io.github.tgkit.plugin;
  exports io.github.tgkit.security.antispam;
  exports io.github.tgkit.security.audit;
  exports io.github.tgkit.security.captcha;
  exports io.github.tgkit.security.ratelimit;
  exports io.github.tgkit.security.rbac;
  exports io.github.tgkit.security.secret;
}
