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
package io.lonmstalker.tgkit.core.resource;

import java.io.IOException;
import java.io.InputStream;
import org.checkerframework.checker.nullness.qual.NonNull;

public interface ResourceLoader {

  /** Открывает поток к содержимому. */
  @NonNull InputStream open() throws IOException;

  /** Идентификатор/имя (используется для логов и чтобы понять расширение). */
  @NonNull String id();

  /* sugar-helpers */
  default byte[] bytes() throws IOException {
    try (InputStream is = open()) {
      return is.readAllBytes();
    }
  }

  default String text() throws IOException {
    return new String(bytes());
  }
}
