module io.lonmstalker.tgkit.boot {
  requires io.lonmstalker.tgkit.core;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires static io.github.tgkit.security;
  requires static io.lonmstalker.tgkit.observability;

  exports io.lonmstalker.tgkit.boot;
}
