package io.lonmstalker.tgkit.plugin;

import org.checkerframework.checker.nullness.qual.NonNull;

import java.net.URL;
import java.net.URLClassLoader;

class ChildFirstURLClassLoader extends URLClassLoader {

    ChildFirstURLClassLoader(@NonNull URL[] urls,
                             @NonNull ClassLoader parent) {
        super(urls, parent);
    }

    @Override
    protected @NonNull Class<?> loadClass(@NonNull String name, boolean resolve) throws ClassNotFoundException {
        synchronized (getClassLoadingLock(name)) {
            try {
                return findClass(name);
            } catch (ClassNotFoundException ignore) {
                return super.loadClass(name, resolve);
            }
        }
    }
}