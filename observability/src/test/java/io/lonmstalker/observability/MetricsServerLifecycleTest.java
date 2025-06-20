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
package io.lonmstalker.observability;

import io.lonmstalker.observability.impl.PrometheusMetricsServer;
import java.net.ServerSocket;
import org.junit.jupiter.api.*;

class MetricsServerLifecycleTest {

  @Test
  void serverBindsAndReleasesPort() throws Exception {
    int port = 9188;
    try (var srv = PrometheusMetricsServer.builder().port(port).build()) {
      Assertions.assertFalse(isFree(port));
    }
    Assertions.assertTrue(isFree(port));
  }

  /* helper */
  private static boolean isFree(int p) {
    try (var s = new ServerSocket(p)) {
      return true;
    } catch (Exception e) {
      return false;
    }
  }
}
