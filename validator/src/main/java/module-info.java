module io.lonmstalker.tgkit.validator {
  requires io.lonmstalker.tgkit.api;
  requires telegrambots;
  requires telegrambots.meta;
  requires static org.checkerframework.checker.qual;
  requires language.detector;
  requires org.apache.tika.langdetect.optimaize;
  requires com.google.common;

  exports io.lonmstalker.tgkit.validator.impl;
  exports io.lonmstalker.tgkit.validator.language;
  exports io.lonmstalker.tgkit.validator.moderation;
}
