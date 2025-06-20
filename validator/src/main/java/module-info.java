module io.lonmstalker.tgkit.validator {
  requires io.lonmstalker.tgkit.api;
  requires org.telegram.telegrambots;
  requires language.detector;
  requires org.apache.tika.langdetect.optimaize;
  requires com.google.common;

  exports io.lonmstalker.tgkit.validator.impl;
  exports io.lonmstalker.tgkit.validator.language;
  exports io.lonmstalker.tgkit.validator.moderation;
}
