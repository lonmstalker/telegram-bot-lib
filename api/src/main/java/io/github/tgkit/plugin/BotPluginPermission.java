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

public enum BotPluginPermission {
  READ_UPDATES(1L),
  SEND_MESSAGES(1L << 1),
  EDIT_MESSAGES(1L << 2),
  DELETE_MESSAGES(1L << 3),
  SCHEDULE_TASKS(1L << 4),
  NETWORK_IO(1L << 5);

  public final long mask;

  BotPluginPermission(long mask) {
    this.mask = mask;
  }
}
