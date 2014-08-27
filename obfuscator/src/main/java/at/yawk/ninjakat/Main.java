package at.yawk.ninjakat;

import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import at.yawk.ninjakat.rename.path.ClassPath;
import at.yawk.ninjakat.rename.path.DirectoryClassPath;
import at.yawk.ninjakat.rename.remap.*;
import at.yawk.ninjakat.rename.scan.Scanner;
import at.yawk.ninjakat.rename.scan.ScanningClassVisitor;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.net.URI;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author yawkat
 */
@Slf4j
public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        Log.init();

        String from = args[0];
        String to = args[1];

        ClassPath path = DirectoryClassPath.forJar(Optional.<ClassPath>empty(), Paths.get(from));

        Scanner scanner = new Scanner(path);

        path.getClasses().forEach(c -> {
            ScanningClassVisitor visitor = new ScanningClassVisitor(null, scanner);
            c.accept(visitor);
        });

        log.info("------------");

        log.info("Aliases:    " + scanner.getAliases());
        log.info("Identities: " + scanner.getIdentifiers());

        log.info("------------");

        MappingGenerator generator = (ident, meta, check) -> {
            if (ident instanceof MethodInfo) {
                if (((MethodInfo) ident).getDescriptor().getName().equals("main")) {
                    return Optional.empty();
                }
            }

            String name;
            int i = 0;
            do {
                name = "c" + (i++);
            } while (!check.test(name));
            return Optional.of(name);
        };

        Remapper remapper = new BasicRemapper(scanner, new SafeMappingGenerator(generator));

        Path toPath = Paths.get(to);

        Files.deleteIfExists(toPath);

        log.info("-----> Running remapper -> " + URI.create("jar:" + toPath.toUri()));

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("create", "true");
        try (FileSystem out = FileSystems.newFileSystem(URI.create("jar:" + toPath.toUri()), properties)) {
            path.getClasses().forEach(c -> {
                ClassNode next = new ClassNode();
                RemappingClassVisitor visitor = new RemappingClassVisitor(next, remapper);
                c.accept(visitor);

                ClassWriter writer = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
                next.accept(writer);
                try (OutputStream dump = Files.newOutputStream(out.getPath(next.name.replace('.', '/') + ".class"))) {
                    dump.write(writer.toByteArray());
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }
}
