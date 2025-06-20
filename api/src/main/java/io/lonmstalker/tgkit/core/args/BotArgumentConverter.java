package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.BotRequest;
import io.lonmstalker.tgkit.core.exception.BotApiException;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface BotArgumentConverter<T, D> {

  @NonNull D convert(@NonNull String raw, @NonNull Context<T> ctx) throws BotApiException;

  default boolean isUpdate() {
    return false;
  }

  default boolean isBotRequest() {
    return false;
  }

  final class UpdateConverter implements BotArgumentConverter<Update, Update> {

    @Override
    public @NonNull Update convert(@NonNull String raw, @NonNull Context<Update> ctx) {
      return ctx.data();
    }

    @Override
    public boolean isUpdate() {
      return true;
    }
  }

  final class RequestConverter
      implements BotArgumentConverter<BotRequest<Object>, BotRequest<Object>> {

    @Override
    public @NonNull BotRequest<Object> convert(
        @NonNull String raw, @NonNull Context<BotRequest<Object>> ctx) throws BotApiException {
      return ctx.data();
    }

    @Override
    public boolean isBotRequest() {
      return BotArgumentConverter.super.isBotRequest();
    }
  }
}
