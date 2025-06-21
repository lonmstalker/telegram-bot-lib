module io.github.tgkit.boot {
  requires io.github.tgkit.core;
  requires io.github.tgkit.api;
  requires spring.boot;
  requires spring.boot.autoconfigure;
  requires spring.context;
  requires spring.beans;
  requires static io.github.tgkit.security;
  requires static io.github.tgkit.observability;

  exports io.github.tgkit.boot;
}
