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
  exports io.lonmstalker.tgkit.security.ratelimit.impl to
      io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.security.rbac;
  exports io.lonmstalker.tgkit.security.secret;
  exports io.lonmstalker.tgkit.security.config;
}
