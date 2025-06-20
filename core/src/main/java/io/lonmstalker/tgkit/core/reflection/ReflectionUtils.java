package io.lonmstalker.tgkit.core.reflection;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReflectionUtils {

  /** Создаёт экземпляр указанного класса, учитывая возможный метод getInstance(). */
  @SuppressWarnings({"return", "unchecked", "argument"})
  public static <T> @NonNull T newInstance(@NonNull Class<T> clazz) {
    try {
      Method getInstance = clazz.getDeclaredMethod("getInstance");
      if (Modifier.isStatic(getInstance.getModifiers())) {
        getInstance.setAccessible(true);
        return (T) getInstance.invoke(null);
      }
    } catch (NoSuchMethodException ignored) {
      // no getInstance method
    } catch (IllegalAccessException | InvocationTargetException e) {
      throw new BotApiException("Cannot invoke getInstance for " + clazz.getName(), e);
    }

    try {
      var ctor =
          MethodHandles.lookup()
              .findConstructor(clazz, MethodType.methodType(void.class))
              .asType(MethodType.methodType(Object.class));
      return (T) ctor.invokeExact();
    } catch (Throwable e) {
      throw new BotApiException("Cannot instantiate " + clazz.getName(), e);
    }
  }
}
