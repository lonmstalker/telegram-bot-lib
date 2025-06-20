/*
 * Copyright 2025 TgKit Team
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
package io.lonmstalker.tgkit.security.captcha;

import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;

public enum MathCaptchaOperations {
  PLUS("+", Integer::sum),
  MINUS("-", (a, b) -> a - b),
  MULT("*", (a, b) -> a * b),
  DIV("/", (a, b) -> b == 0 ? 0 : a / b); // защита от /0

  public static final List<MathCaptchaOperations> OPERATIONS =
      Arrays.asList(PLUS, MINUS, MULT, DIV);

  private final String symbol;
  private final BiFunction<Integer, Integer, Integer> fn;

  MathCaptchaOperations(String symbol, BiFunction<Integer, Integer, Integer> fn) {
    this.symbol = symbol;
    this.fn = fn;
  }

  public int apply(int a, int b) {
    return fn.apply(a, b);
  }

  public String symbol() {
    return symbol;
  }
}
