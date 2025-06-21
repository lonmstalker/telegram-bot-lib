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
module io.github.tgkit.security {
  requires io.github.tgkit.core;
  requires org.slf4j;
  requires com.github.benmanes.caffeine;
  requires io.github.jopenlibs.vault.java.driver;
  requires jedis;

  exports io.github.tgkit.security;
  exports io.github.tgkit.security.antispam;
  exports io.github.tgkit.security.audit;
  exports io.github.tgkit.security.captcha;
  exports io.github.tgkit.security.captcha.provider;
  exports io.github.tgkit.security.event;
  exports io.github.tgkit.security.init;
  exports io.github.tgkit.security.ratelimit;
  exports io.github.tgkit.security.ratelimit.impl to
      io.github.tgkit.plugin;
  exports io.github.tgkit.security.rbac;
  exports io.github.tgkit.security.secret;
  exports io.github.tgkit.security.config;
}
