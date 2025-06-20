/*
 * Copyright (C) 2024 the original author or authors.
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
package io.lonmstalker.tgkit.core.dsl;

import io.lonmstalker.tgkit.core.config.BotGlobalConfig;
import io.lonmstalker.tgkit.core.dsl.context.DSLContext;
import io.lonmstalker.tgkit.core.dsl.validator.CaptionValidator;
import io.lonmstalker.tgkit.core.dsl.validator.FileSizeValidator;
import io.lonmstalker.tgkit.core.parse_mode.ParseMode;
import io.lonmstalker.tgkit.core.parse_mode.Sanitizer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/** Построитель отправки фото. */
@SuppressWarnings("initialization.fields.uninitialized")
public final class PhotoBuilder extends BotDSL.CommonBuilder<PhotoBuilder, SendPhoto> {
  private static final CaptionValidator CAPTION_VALIDATOR = new CaptionValidator();
  private static final FileSizeValidator FILE_SIZE_VALIDATOR =
      new FileSizeValidator(20 * 1024 * 1024); // 20 MB фото
  private final InputFile file;
  private String caption;
  private ParseMode parseMode;
  private Boolean sanitize;

  @SuppressWarnings("initialization.fields.uninitialized")
  PhotoBuilder(@NonNull DSLContext ctx, @NonNull InputFile file) {
    super(ctx);
    this.file = file;
  }

  public @NonNull PhotoBuilder parseMode(@NonNull ParseMode mode) {
    this.parseMode = mode;
    return this;
  }

  /** Подпись к фото. */
  public @NonNull PhotoBuilder caption(@NonNull String text) {
    this.caption = text;
    return this;
  }

  public @NonNull PhotoBuilder sanitize(boolean sanitize) {
    this.sanitize = sanitize;
    return this;
  }

  @Override
  public @NonNull SendPhoto build() {
    requireChatId();

    ParseMode p = parseMode != null ? parseMode : BotGlobalConfig.INSTANCE.dsl().getParseMode();
    boolean s = this.sanitize != null ? this.sanitize : BotGlobalConfig.INSTANCE.dsl().isSanitize();

    String t = s ? Sanitizer.sanitize(caption, p) : caption;

    CAPTION_VALIDATOR.validate(t);
    FILE_SIZE_VALIDATOR.validate(file);

    SendPhoto photo = new SendPhoto(String.valueOf(chatId), file);
    photo.setCaption(t);
    photo.setDisableNotification(disableNotif);

    if (keyboard != null) {
      photo.setReplyMarkup(keyboard.build());
    }

    return photo;
  }
}
