package io.lonmstalker.tgkit.plugin;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.checkerframework.checker.nullness.qual.NonNull;

/** ClassLoader с child-first стратегией и кешированием загруженных классов для ускорения lookup. */
public class ChildFirstURLClassLoader extends URLClassLoader {
  private final Map<String, Class<?>> classCache = new ConcurrentHashMap<>();

  public ChildFirstURLClassLoader(@NonNull URL[] urls, @NonNull ClassLoader parent) {
    super(urls, parent);
  }

  @Override
  protected Class<?> loadClass(@NonNull String name, boolean resolve)
      throws ClassNotFoundException {
    // Попробовать взять из кеша
    Class<?> cached = classCache.get(name);
    if (cached != null) {
      return cached;
    }

    synchronized (getClassLoadingLock(name)) {
      try {
        Class<?> cls = findClass(name);
        classCache.put(name, cls);
        return cls;
      } catch (ClassNotFoundException ignored) {
        Class<?> parentClass = super.loadClass(name, resolve);
        classCache.put(name, parentClass);
        return parentClass;
      }
    }
  }
}
