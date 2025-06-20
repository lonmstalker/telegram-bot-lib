package io.lonmstalker.tgkit.security.rbac;

import io.lonmstalker.tgkit.core.BotCommand;
import io.lonmstalker.tgkit.core.loader.BotCommandFactory;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.stream.Collectors;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

public final class RoleBotCommandFactory implements BotCommandFactory<RequiresRole> {

  @Override
  public @NonNull Class<RequiresRole> annotationType() {
    return RequiresRole.class;
  }

  @Override
  public void apply(
      @NonNull BotCommand<?> command, @NonNull Method method, @Nullable RequiresRole ann) {

    var anns =
        Arrays.stream(method.getAnnotationsByType(RequiresRole.class)).collect(Collectors.toSet());
    anns.addAll(Arrays.asList(method.getDeclaringClass().getAnnotationsByType(RequiresRole.class)));

    if (anns.isEmpty()) {
      return;
    }

    var roles = anns.stream().flatMap(a -> Arrays.stream(a.value())).collect(Collectors.toSet());

    command.addInterceptor(new RoleInterceptor(roles));
  }
}
