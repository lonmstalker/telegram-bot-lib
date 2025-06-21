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
module io.github.tgkit.observability {
  requires io.github.tgkit.core;
  requires io.micrometer.core;
  requires io.opentelemetry.api;
  requires io.opentelemetry.sdk;
  requires io.opentelemetry.exporter.logging;
  requires ch.qos.logback.classic;
  requires io.micrometer.registry.prometheus;
  requires io.prometheus.simpleclient_httpserver;

  exports io.github.observability;
  exports io.github.observability.impl to
      io.github.tgkit.plugin;
}
