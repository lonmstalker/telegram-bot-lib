package io.lonmstalker.observability;

import io.lonmstalker.observability.impl.PrometheusMetricsServer;
import org.junit.jupiter.api.*;
import java.net.ServerSocket;

class MetricsServerLifecycleTest {

    @Test void serverBindsAndReleasesPort() throws Exception {
        int port = 9188;
        try (var srv = PrometheusMetricsServer.builder()
                .port(port)
                .build()) {
           Assertions.assertFalse(isFree(port));
        }
        Assertions.assertTrue(isFree(port));
    }

    /* helper */
    private static boolean isFree(int p) {
        try (var s = new ServerSocket(p)) { return true; }
        catch (Exception e) { return false; }
    }
}
