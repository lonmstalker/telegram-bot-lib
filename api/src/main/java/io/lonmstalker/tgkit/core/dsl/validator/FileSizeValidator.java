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
package io.lonmstalker.tgkit.core.dsl.validator;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import io.lonmstalker.tgkit.core.validator.Validator;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.objects.InputFile;

/* File size in bytes */
public final class FileSizeValidator implements Validator<InputFile> {
  private final long maxBytes;

  public FileSizeValidator(long maxBytes) {
    this.maxBytes = maxBytes;
  }

  @Override
  public void validate(@Nullable InputFile f) {
    if (f == null) {
      throw new BotApiException("Input file is null");
    }
    if (f.getNewMediaFile() != null && f.getNewMediaFile().length() > maxBytes) {
      throw new BotApiException("File exceeds " + maxBytes / 1024 / 1024 + " MB");
    }
  }
}
