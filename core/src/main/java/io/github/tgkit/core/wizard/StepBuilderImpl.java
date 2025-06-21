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
package io.github.tgkit.core.wizard;

import io.github.tgkit.core.BotRequest;
import io.github.tgkit.core.i18n.MessageKey;
import io.github.tgkit.core.validator.Validator;
import io.github.tgkit.validator.impl.LocationValidators;
import io.github.tgkit.validator.impl.PhotoValidators;
import io.github.tgkit.validator.impl.TextValidators;
import io.github.tgkit.validator.impl.VideoValidators;
import java.time.Duration;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Predicate;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.CallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Location;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import org.telegram.telegrambots.meta.api.objects.Video;
import org.telegram.telegrambots.meta.api.objects.VideoNote;

/**
 * {@link StepBuilder} с поддержкой всех типов ввода Telegram.
 *
 * <p>Каждый <code>expect*</code> добавляет две проверки: <br>
 * &nbsp;• <em>type-validator</em> — убеждаемся, что в {@link BotRequest#data()} пришёл нужный тип;
 * <br>
 * &nbsp;• <em>content-validator</em> — бизнес-ограничения (размер, разрешение и т.д.).
 */
final class StepBuilderImpl<M, I, O> implements StepBuilder<M, I, O> {

  private final @NonNull StepDefinition<M, I, O> def;

  StepBuilderImpl(@NonNull StepDefinition<M, I, O> def) {
    this.def = def;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> ask(@NonNull MessageKey... keys) {
    def.getQuestionKeys().addAll(List.of(keys));
    return this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, String, String> expectText() {
    def.setParser(req -> (I) ((Message) req.data()).getText());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof Message msg && msg.hasText(),
            MessageKey.of("error.text.required")));
    def.getValidators().add((Validator<O>) TextValidators.notBlank());
    def.getValidators().add((Validator<O>) TextValidators.maxLength());
    return (StepBuilder<M, String, String>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, String, Integer> expectInt() {
    def.setParser(
        req -> (I) Integer.valueOf(Integer.parseInt(((Message) req.data()).getText().trim())));
    def.setTypeValidator(
        Validator.of(
            r ->
                r.data() instanceof Message msg
                    && msg.hasText()
                    && NumberUtils.isDigits(msg.getText()),
            MessageKey.of("error.number.required")));
    return (StepBuilder<M, String, Integer>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, ?, List<PhotoSize>> expectPhoto() {
    def.setParser(req -> (I) ((Message) req.data()).getPhoto());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof Message msg && msg.hasPhoto(),
            MessageKey.of("error.photo.required")));
    def.getValidators().add((Validator<O>) PhotoValidators.minResolution(64, 64));
    def.getValidators().add((Validator<O>) PhotoValidators.maxSizeKb(5120));
    return (StepBuilder<M, ?, List<PhotoSize>>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, ?, Video> expectVideo() {
    def.setParser(req -> (I) ((Message) req.data()).getVideo());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof Message msg && msg.hasVideo(),
            MessageKey.of("error.video.required")));
    def.getValidators().add((Validator<O>) VideoValidators.maxSizeKb(50L * FileUtils.ONE_MB));
    def.getValidators().add((Validator<O>) VideoValidators.safeSearch());
    return (StepBuilder<M, ?, Video>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, ?, VideoNote> expectVideoNote() {
    def.setParser(req -> (I) ((Message) req.data()).getVideoNote());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof Message msg && msg.hasVideoNote(),
            MessageKey.of("error.vnote.required")));
    def.getValidators().add((Validator<O>) VideoValidators.maxDurationSec(60));
    return (StepBuilder<M, ?, VideoNote>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, ?, Location> expectLocation() {
    def.setParser(req -> (I) ((Message) req.data()).getLocation());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof Message msg && msg.hasLocation(),
            MessageKey.of("error.location.required")));
    def.getValidators().add((Validator<O>) LocationValidators.inBounds(/*world*/ ));
    return (StepBuilder<M, ?, Location>) this;
  }

  @Override
  @SuppressWarnings("unchecked")
  public @NonNull StepBuilder<M, ?, String> expectButtons(@NonNull List<String> payloads) {
    def.setParser(req -> (I) ((CallbackQuery) req.data()).getData());
    def.setTypeValidator(
        Validator.of(
            r -> r.data() instanceof CallbackQuery cb && payloads.contains(cb.getData()),
            MessageKey.of("error.button.invalid")));
    return (StepBuilder<M, ?, String>) this;
  }

  @Override
  public <V extends Validator<O>> @NonNull StepBuilder<M, I, O> validate(@NonNull V v) {
    def.getValidators().add(v);
    return this;
  }

  @Override
  public void save(@NonNull BiConsumer<M, O> setter) {
    def.setSetter(setter);
  }

  @Override
  public @NonNull StepBuilder<M, I, O> allowBack(@NonNull BiConsumer<BotRequest<?>, M> h) {
    def.setCanBack(true);
    def.setOnBack(h);
    return this;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> allowSkip(@NonNull BiConsumer<BotRequest<?>, M> h) {
    def.setCanSkip(true);
    def.setOnSkip(h);
    return this;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> allowCancel(@NonNull BiConsumer<BotRequest<?>, M> h) {
    def.setCanCancel(true);
    def.setOnCancel(h);
    return this;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> nextIf(@NonNull Predicate<M> c, @NonNull String id) {
    def.setNextSupplier(m -> c.test(m) ? id : null);
    return this;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> onTimeout(@NonNull Duration d, @NonNull MessageKey k) {
    def.setTimeout(d);
    def.setReminderKey(k);
    return this;
  }

  @Override
  public @NonNull StepBuilder<M, I, O> preFinish(@NonNull Predicate<M> chk, @NonNull String fail) {
    def.setPreFinishChecker(chk);
    def.setPreFinishFailStepId(fail);
    return this;
  }
}
