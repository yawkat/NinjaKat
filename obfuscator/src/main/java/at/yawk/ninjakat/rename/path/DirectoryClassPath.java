package at.yawk.ninjakat.rename.path;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import at.yawk.ninjakat.rename.unsafe.UnsafePredicate;
import com.google.common.collect.Iterables;
import java.io.IOException;
import java.io.InputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Optional;
import java.util.stream.Stream;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author yawkat
 */
public class DirectoryClassPath extends ClassPath {
    private final Path directory;
    private final FileCache<ClassNode> cache = FileCache.create(p -> {
        try (InputStream in = Files.newInputStream(p)) {
            ClassReader reader = new ClassReader(in);
            ClassNode memory = new ClassNode();
            reader.accept(memory, ClassReader.EXPAND_FRAMES);
            return memory;
        }
    });

    public DirectoryClassPath(Optional<ClassPath> parent, Path directory) {
        super(parent);
        this.directory = directory;
    }

    public static ClassPath forJar(Optional<ClassPath> parent, Path jarPath) throws IOException {
        URI uri = URI.create("jar:" + jarPath.toUri());
        FileSystem fs = FileSystems.newFileSystem(uri, new HashMap<>());
        return new DirectoryClassPath(parent, Iterables.getOnlyElement(fs.getRootDirectories())) {
            @Override
            public void close() throws Exception {
                fs.close();
                super.close();
            }
        };
    }

    @Override
    public Optional<ClassNode> findClass(ClassDescriptor descriptor) {
        String sub = descriptor.getName().replace('.', '/') + ".class";
        Path entry = directory.resolve(sub);
        if (Files.exists(entry)) {
            return Optional.of(cache.get(entry));
        }

        return super.findClass(descriptor);
    }

    @Override
    public Stream<ClassNode> getClasses() {
        return Stream.concat(
                super.getClasses(),
                listRecur(directory)
                        .filter(UnsafePredicate.unsafe(p -> Files.isRegularFile(p) && p.toString().endsWith(".class")))
                        .map(cache::get)
        );
    }

    private static Stream<Path> listRecur(Path p) {
        if (Files.isDirectory(p)) {
            try {
                return Files.list(p).flatMap(DirectoryClassPath::listRecur);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        } else {
            return Stream.of(p);
        }
    }
}
