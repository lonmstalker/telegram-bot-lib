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
