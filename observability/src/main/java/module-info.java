module io.lonmstalker.tgkit.observability {
  requires io.lonmstalker.tgkit.core;
  requires io.micrometer.core;
  requires io.opentelemetry.api;
  requires io.opentelemetry.sdk;
  requires io.opentelemetry.exporter.logging;
  requires ch.qos.logback.classic;
  requires io.micrometer.registry.prometheus;
  requires io.prometheus.simpleclient_httpserver;

  exports io.lonmstalker.observability;
  exports io.lonmstalker.observability.impl to
      io.lonmstalker.tgkit.plugin;
}
