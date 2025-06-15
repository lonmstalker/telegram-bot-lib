package demo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.stream.Stream;

public class JarHelper {
    public static Path buildJar(Path classesDir, Path outputJar) throws IOException {
        try (JarOutputStream jos = new JarOutputStream(Files.newOutputStream(outputJar))) {
            try (Stream<Path> stream = Files.walk(classesDir)) {
                stream.filter(Files::isRegularFile).forEach(p -> {
                    String entryName = classesDir.relativize(p).toString().replace("\\", "/");
                    try {
                        jos.putNextEntry(new JarEntry(entryName));
                        Files.copy(p, jos);
                        jos.closeEntry();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
        return outputJar;
    }
}
