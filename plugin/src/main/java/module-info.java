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
