module io.lonmstalker.tgkit.plugin {
  requires io.lonmstalker.tgkit.core;
  requires io.lonmstalker.tgkit.observability;
  requires io.lonmstalker.tgkit.security;
  requires org.slf4j;
  requires com.fasterxml.jackson.dataformat.yaml;
  requires static lombok;
  requires static org.checkerframework.checker.qual;

  exports io.lonmstalker.tgkit.plugin;
  exports io.lonmstalker.tgkit.plugin.annotation;
  exports io.lonmstalker.tgkit.plugin.sort;
}
