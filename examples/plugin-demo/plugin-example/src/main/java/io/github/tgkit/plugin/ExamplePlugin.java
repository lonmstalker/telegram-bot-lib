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
package io.github.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExamplePlugin implements BotPlugin {

  private static final Logger log = LoggerFactory.getLogger(ExamplePlugin.class);

  @Override
  public void onLoad(@NonNull BotPluginContext ctx) {
    log.info("onLoad");
  }

  @Override
  public void start() {
    log.info("start");
  }

  @Override
  public void beforeStop() {
    log.info("beforeStop");
  }

  @Override
  public void stop() {
    log.info("stop");
  }

  @Override
  public void afterStop() {
    log.info("afterStop");
  }

  @Override
  public void onUnload() {
    log.info("onUnload");
  }
}
