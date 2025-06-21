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
package io.github.tgkit.core.dsl;

import io.github.tgkit.core.dsl.context.DSLContext;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.telegram.telegrambots.meta.api.methods.AnswerInlineQuery;
import org.telegram.telegrambots.meta.api.objects.inlinequery.inputmessagecontent.InputTextMessageContent;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResult;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultArticle;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultPhoto;
import org.telegram.telegrambots.meta.api.objects.inlinequery.result.InlineQueryResultsButton;

/** Построитель результатов инлайн‑запроса. */
public final class InlineResultBuilder
    extends BotDSL.CommonBuilder<InlineResultBuilder, AnswerInlineQuery> {
  private final @NonNull DSLContext ctx;
  private final List<InlineQueryResult> list = new ArrayList<>();
  private @Nullable Boolean isPersonal;
  private @Nullable String inlineQueryId;
  private @Nullable Integer cacheTime;
  private @Nullable String nextOffset;
  private @Nullable InlineQueryResultsButton button;

  InlineResultBuilder(@NonNull DSLContext ctx) {
    super(ctx);
    this.ctx = ctx;
  }

  /** Статья. */
  public InlineResultBuilder article(
      @NonNull String id, @NonNull String title, @NonNull String text) {
    InlineQueryResultArticle a = new InlineQueryResultArticle();
    a.setId(id);
    a.setTitle(title);
    a.setInputMessageContent(new InputTextMessageContent(text));
    list.add(a);
    return this;
  }

  /** Статья с текстом из i18n. */
  public InlineResultBuilder articleKey(
      @NonNull String id,
      @NonNull String titleKey,
      @NonNull String textKey,
      @NonNull Object... args) {
    return article(
        id,
        ctx.service().localizer().get(titleKey, args),
        ctx.service().localizer().get(textKey, args));
  }

  /** Фото. */
  public @NonNull InlineResultBuilder photo(
      @NonNull String id, @NonNull String url, @NonNull String thumb) {
    InlineQueryResultPhoto p = new InlineQueryResultPhoto();
    p.setId(id);
    p.setPhotoUrl(url);
    p.setThumbnailUrl(thumb);
    list.add(p);
    return this;
  }

  public @NonNull InlineResultBuilder isPersonal(@NonNull Boolean isPersonal) {
    this.isPersonal = isPersonal;
    return this;
  }

  public @NonNull InlineResultBuilder cacheTime(@NonNull Integer cacheTime) {
    this.cacheTime = cacheTime;
    return this;
  }

  public @NonNull InlineResultBuilder nextOffset(@NonNull String nextOffset) {
    this.nextOffset = nextOffset;
    return this;
  }

  public @NonNull InlineResultBuilder inlineQueryId(@NonNull String inlineQueryId) {
    this.inlineQueryId = inlineQueryId;
    return this;
  }

  public @NonNull InlineResultBuilder button(
      @NonNull Consumer<InlineQueryResultsButton.InlineQueryResultsButtonBuilder>
          buttonBuilderConsumer) {
    var builder = InlineQueryResultsButton.builder();
    buttonBuilderConsumer.accept(builder);
    this.button = builder.build();
    return this;
  }

  @Override
  public @NonNull AnswerInlineQuery build() {
    if (inlineQueryId == null) {
      throw new IllegalStateException("You must specify an inline query id");
    }

    AnswerInlineQuery query = new AnswerInlineQuery();
    query.setResults(list);
    query.setCacheTime(cacheTime);
    query.setNextOffset(nextOffset);
    query.setIsPersonal(isPersonal);
    query.setInlineQueryId(inlineQueryId);

    if (button != null) {
      query.setButton(button);
    }

    return query;
  }
}
