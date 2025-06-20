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

import org.checkerframework.checker.nullness.qual.NonNull;

/** Построитель форматированного текста Markdown. */
public final class RichText {
  private final StringBuilder sb = new StringBuilder();

  public static @NonNull RichText text() {
    return new RichText();
  }

  /** Полужирный текст. */
  public @NonNull RichText bold(@NonNull String text) {
    sb.append("**").append(text).append("**");
    return this;
  }

  /** Ссылка. */
  public @NonNull RichText url(@NonNull String label, @NonNull String url) {
    sb.append("[").append(label).append("](").append(url).append(")");
    return this;
  }

  @Override
  public String toString() {
    return sb.toString();
  }
}
