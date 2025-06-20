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
module io.lonmstalker.tgkit.plugin {
  requires io.lonmstalker.tgkit.core;
  requires io.lonmstalker.tgkit.observability;
  requires io.lonmstalker.tgkit.security;
  requires org.slf4j;
  requires com.fasterxml.jackson.dataformat.yaml;

  exports io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.plugin.annotation;
  exports io.lonmstalker.tgkit.plugin.internal to
      io.lonmstalker.tgkit.core;
}
