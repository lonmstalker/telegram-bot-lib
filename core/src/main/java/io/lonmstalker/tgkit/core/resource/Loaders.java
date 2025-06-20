package io.lonmstalker.tgkit.core.resource;

import java.io.FileNotFoundException;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import lombok.experimental.UtilityClass;
import org.checkerframework.checker.nullness.qual.NonNull;

@UtilityClass
public class Loaders {

  public @NonNull ResourceLoader load(@NonNull String path) {
    if (path.startsWith("classpath:")) {
      return classpath(path.replace("classpath:", ""));
    }
    if (path.startsWith("file:")) {
      return file(Path.of(path.replace("file:", "")));
    }
    if (path.startsWith("http://") || path.startsWith("https://")) {
      return url(URI.create(path.replace("http://", "").replace("https://", "")));
    }
    if (path.startsWith("jar:")) {
      String[] split = path.replace("jar:", "").split("!");
      return jar(Path.of(split[0]), split[1]);
    }
    throw new IllegalArgumentException("Not a valid path: " + path);
  }

  /** classpath:"/cfg.yml" ─ ищет ресурс в ClassLoader-е текущего класса. */
  public static ResourceLoader classpath(@NonNull String path) {
    return new ResourceLoader() {

      @Override
      public @NonNull InputStream open() throws IOException {
        InputStream in =
            Loaders.class.getResourceAsStream(path.startsWith("/") ? path : "/" + path);
        if (in == null) {
          throw new FileNotFoundException("Classpath resource " + path);
        }
        return in;
      }

      @Override
      public @NonNull String id() {
        return "cp:" + path;
      }

      @Override
      public String toString() {
        return id();
      }
    };
  }

  /** file:"/etc/app.yml" */
  public static ResourceLoader file(@NonNull Path p) {
    return new ResourceLoader() {

      @Override
      public @NonNull InputStream open() throws IOException {
        return Files.newInputStream(p.toAbsolutePath());
      }

      @Override
      public @NonNull String id() {
        return "file:" + p;
      }

      @Override
      public String toString() {
        return id();
      }
    };
  }

  /** url:"<a href="https://example.com/cfg.json">...</a>" */
  public static ResourceLoader url(@NonNull URI uri) {
    return new ResourceLoader() {

      @Override
      public @NonNull InputStream open() throws IOException {
        return uri.toURL().openStream();
      }

      @Override
      public @NonNull String id() {
        return uri.toString();
      }

      @Override
      public String toString() {
        return id();
      }
    };
  }

  /*──────────────────────────────────────────────────────────────────*/
  /* 3️⃣  jar-loader: Path к .jar + internal path "config/app.yml"    */
  /*──────────────────────────────────────────────────────────────────*/
  public static ResourceLoader jar(@NonNull Path jar, @NonNull String inside) {
    return new ResourceLoader() {

      @Override
      public @NonNull InputStream open() throws IOException {
        JarFile jf = new JarFile(jar.toFile());
        JarEntry e = jf.getJarEntry(inside);
        if (e == null) {
          jf.close();
          throw new FileNotFoundException("Entry " + inside + " not found in " + jar);
        }
        /* оборачиваем stream, чтобы при close() закрыть и JarFile */
        return new FilterInputStream(jf.getInputStream(e)) {
          @Override
          public void close() throws IOException {
            super.close();
            jf.close();
          }
        };
      }

      @Override
      public @NonNull String id() {
        return "jar:" + jar + "!" + inside;
      }

      @Override
      public String toString() {
        return id();
      }
    };
  }
}
