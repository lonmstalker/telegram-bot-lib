package io.lonmstalker.tgkit.core.experimental;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Помечает API как экспериментальный.
 *
 * <p>Элементы, аннотированные {@code @Incubating}, могут измениться или быть удалены без
 * предупреждения.
 *
 * <p>Пример использования:
 *
 * <pre>{@code
 * @Incubating
 * public class ExperimentalApi { }
 *
 * @Incubating
 * public void experimentalMethod() { }
 * }</pre>
 */
@Documented
@Retention(RetentionPolicy.CLASS)
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Incubating {}
