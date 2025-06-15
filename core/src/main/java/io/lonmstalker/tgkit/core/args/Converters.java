package io.lonmstalker.tgkit.core.args;

import io.lonmstalker.tgkit.core.exception.BotApiException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@SuppressWarnings("unchecked")
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Converters {
    private static final Map<Class<?>, BotArgumentConverter<?>> BY_TYPE = new ConcurrentHashMap<>();
    private static final Map<Class<? extends BotArgumentConverter<?>>, BotArgumentConverter<?>> BY_CLASS = new ConcurrentHashMap<>();

    static {
        ServiceLoader.load(BotArgumentConverter.class).forEach(c -> {
            Class<?> type = extractType(c.getClass());
            BY_TYPE.put(type, c);
            BY_CLASS.put((Class<? extends BotArgumentConverter<?>>) c.getClass(), c);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static <T> @NonNull BotArgumentConverter<T> getByType(Class<T> type) {
        if (type.isPrimitive()) {
            type = (Class<T>) wrapPrimitive(type);
        }
        BotArgumentConverter<?> converter = BY_TYPE.get(type);
        if (converter != null) {
            return (BotArgumentConverter<T>) converter;
        }
        if (Enum.class.isAssignableFrom(type)) {
            Class<T> finalType = type;
            return (raw, ctx) -> (T) Enum.valueOf((Class) finalType, raw);
        }
        if (Number.class.isAssignableFrom(type)) {
            return (BotArgumentConverter<T>) numberConverter((Class<? extends Number>) type);
        }
        if (UUID.class.equals(type)) {
            return (BotArgumentConverter<T>) (BotArgumentConverter<?>) (raw, ctx) -> UUID.fromString(raw);
        }
        return (BotArgumentConverter<T>) new BotArgumentConverter.Identity();
    }

    public static <T extends BotArgumentConverter<?>> @NonNull BotArgumentConverter<?> getByClass(Class<T> clazz) {
        return BY_CLASS.computeIfAbsent(clazz, Converters::instantiate);
    }

    private static BotArgumentConverter<?> instantiate(Class<? extends BotArgumentConverter<?>> clazz) {
        try {
            return clazz.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private static Class<?> extractType(Class<?> clazz) {
        for (Type t : clazz.getGenericInterfaces()) {
            if (t instanceof ParameterizedType pt && pt.getRawType() == BotArgumentConverter.class) {
                Type arg = pt.getActualTypeArguments()[0];
                if (arg instanceof Class<?> c) {
                    return c;
                }
            }
        }
        Class<?> sup = clazz.getSuperclass();
        if (sup != null && sup != Object.class) {
            return extractType(sup);
        }
        return Object.class;
    }

    private static Class<?> wrapPrimitive(Class<?> type) {
        return switch (type.getName()) {
            case "int" -> Integer.class;
            case "long" -> Long.class;
            case "double" -> Double.class;
            case "float" -> Float.class;
            case "short" -> Short.class;
            case "byte" -> Byte.class;
            case "boolean" -> Boolean.class;
            case "char" -> Character.class;
            default -> type;
        };
    }

    /**
     * Возвращает конвертер для числовых типов.
     */
    private static BotArgumentConverter<? extends Number> numberConverter(Class<? extends Number> type) {
        // преобразование строковых аргументов в нужный числовой тип
        return (raw, ctx) -> switch (type.getSimpleName()) {
            case "Integer" -> Integer.parseInt(raw);
            case "Long" -> Long.parseLong(raw);
            case "Double" -> Double.parseDouble(raw);
            case "Float" -> Float.parseFloat(raw);
            case "Short" -> Short.parseShort(raw);
            case "Byte" -> Byte.parseByte(raw);
            default -> throw new BotApiException("Unsupported number type: " + type);
        };
    }
}
