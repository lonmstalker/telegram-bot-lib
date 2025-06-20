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
package io.lonmstalker.tgkit.core.bot;

import org.telegram.telegrambots.meta.generics.BackOff;

/** Простая реализация экспоненциальной задержки для TelegramBot. */
public class ExponentialBackOff implements BackOff {
  private static final int DEFAULT_INITIAL_INTERVAL_MILLIS = 500;
  private static final double DEFAULT_RANDOMIZATION_FACTOR = 0.5d;
  private static final double DEFAULT_MULTIPLIER = 1.5d;
  private static final int DEFAULT_MAX_INTERVAL_MILLIS = 900_000;
  private static final int DEFAULT_MAX_ELAPSED_TIME_MILLIS = 3_600_000;

  private int currentIntervalMillis;
  private final int initialIntervalMillis;
  private final double randomizationFactor;
  private final double multiplier;
  private final int maxIntervalMillis;
  private long startTimeNanos;
  private final int maxElapsedTimeMillis;

  public ExponentialBackOff() {
    this(new Builder());
  }

  private ExponentialBackOff(Builder builder) {
    this.initialIntervalMillis = builder.initialIntervalMillis;
    this.randomizationFactor = builder.randomizationFactor;
    this.multiplier = builder.multiplier;
    this.maxIntervalMillis = builder.maxIntervalMillis;
    this.maxElapsedTimeMillis = builder.maxElapsedTimeMillis;

    if (initialIntervalMillis <= 0) {
      throw new IllegalArgumentException("InitialIntervalMillis must not be negative");
    }
    if (maxElapsedTimeMillis <= 0) {
      throw new IllegalArgumentException("MaxElapsedTimeMillis must not be negative");
    }
    if (multiplier < 1.0) {
      throw new IllegalArgumentException("Multiplier must be bigger than 0");
    }
    if (maxIntervalMillis < initialIntervalMillis) {
      throw new IllegalArgumentException(
          "InitialIntervalMillis must be smaller or equal maxIntervalMillis");
    }
    if (randomizationFactor < 0 || randomizationFactor >= 1) {
      throw new IllegalArgumentException("RandomizationFactor must be between 0 and 1");
    }
    reset();
  }

  @Override
  public void reset() {
    currentIntervalMillis = initialIntervalMillis;
    startTimeNanos = nanoTime();
  }

  @Override
  public long nextBackOffMillis() {
    if (getElapsedTimeMillis() > maxElapsedTimeMillis) {
      return maxElapsedTimeMillis;
    }
    int randomInterval =
        getRandomValueFromInterval(randomizationFactor, Math.random(), currentIntervalMillis);
    incrementCurrentInterval();
    return randomInterval;
  }

  private static int getRandomValueFromInterval(
      double randomizationFactor, double random, int currentInterval) {
    double delta = randomizationFactor * currentInterval;
    double minInterval = currentInterval - delta;
    double maxInterval = currentInterval + delta;
    return (int) (minInterval + random * (maxInterval - minInterval + 1));
  }

  private long getElapsedTimeMillis() {
    return (nanoTime() - startTimeNanos) / 1_000_000;
  }

  private void incrementCurrentInterval() {
    if (((double) currentIntervalMillis) >= ((double) maxIntervalMillis) / multiplier) {
      currentIntervalMillis = maxIntervalMillis;
    } else {
      currentIntervalMillis = (int) (currentIntervalMillis * multiplier);
    }
  }

  private long nanoTime() {
    return System.nanoTime();
  }

  public static class Builder {
    private int initialIntervalMillis = DEFAULT_INITIAL_INTERVAL_MILLIS;
    private double randomizationFactor = DEFAULT_RANDOMIZATION_FACTOR;
    private double multiplier = DEFAULT_MULTIPLIER;
    private int maxIntervalMillis = DEFAULT_MAX_INTERVAL_MILLIS;
    private int maxElapsedTimeMillis = DEFAULT_MAX_ELAPSED_TIME_MILLIS;

    public Builder setInitialIntervalMillis(int initialIntervalMillis) {
      this.initialIntervalMillis = initialIntervalMillis;
      return this;
    }

    public Builder setRandomizationFactor(double randomizationFactor) {
      this.randomizationFactor = randomizationFactor;
      return this;
    }

    public Builder setMultiplier(double multiplier) {
      this.multiplier = multiplier;
      return this;
    }

    public Builder setMaxIntervalMillis(int maxIntervalMillis) {
      this.maxIntervalMillis = maxIntervalMillis;
      return this;
    }

    public Builder setMaxElapsedTimeMillis(int maxElapsedTimeMillis) {
      this.maxElapsedTimeMillis = maxElapsedTimeMillis;
      return this;
    }

    public ExponentialBackOff build() {
      return new ExponentialBackOff(this);
    }
  }
}
