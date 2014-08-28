package at.yawk.ninjakat.js;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import at.yawk.ninjakat.rename.descriptor.FieldInfo;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import at.yawk.ninjakat.rename.path.ClassPath;
import at.yawk.ninjakat.rename.path.DirectoryClassPath;
import at.yawk.ninjakat.rename.remap.BasicRemapper;
import at.yawk.ninjakat.rename.remap.MappingGenerator;
import at.yawk.ninjakat.rename.remap.Remapper;
import at.yawk.ninjakat.rename.remap.SafeMappingGenerator;
import at.yawk.ninjakat.rename.scan.Scanner;
import at.yawk.ninjakat.rename.unsafe.UnsafeSupplier;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UncheckedIOException;
import java.lang.invoke.MethodHandle;
import java.net.URI;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;
import jdk.nashorn.internal.runtime.ScriptFunction;
import jdk.nashorn.internal.runtime.Undefined;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author yawkat
 */
@Slf4j
@RequiredArgsConstructor
public class ActionProvider {
    private final Path workingDir;

    public void remap(String inJar,
                      List<String> path,
                      String outJar,
                      Function<ClassDescriptor, Supplier<String>> classMapper,
                      Function<MethodInfo, Supplier<String>> methodMapper,
                      Function<FieldInfo, Supplier<String>> fieldMapper) throws IOException {
        Optional<ClassPath> parent = Optional.empty();
        for (String pathEntry : path) {
            parent = Optional.of(DirectoryClassPath.forJar(parent, resolve(pathEntry)));
        }

        ClassPath managed = DirectoryClassPath.forJar(parent, resolve(inJar));

        Scanner scanner = new Scanner(managed);

        log.info("Scanning classes...");
        managed.getManagedClasses().forEach(scanner::scan);

        MappingGenerator generator = (ident, meta, check) -> {
            if (ident instanceof MethodInfo) {
                if (((MethodInfo) ident).getDescriptor().getName().equals("main")) {
                    return Optional.empty();
                }
            }

            Supplier<String> nameGen;
            if (ident instanceof ClassDescriptor) {
                nameGen = jsToSupplier(classMapper.apply((ClassDescriptor) ident));
            } else if (ident instanceof MethodInfo) {
                nameGen = jsToSupplier(methodMapper.apply((MethodInfo) ident));
            } else if (ident instanceof FieldInfo) {
                nameGen = jsToSupplier(fieldMapper.apply((FieldInfo) ident));
            } else {
                log.error("Unsupported identifier " + ident);
                return Optional.empty();
            }

            if (nameGen == null) {
                return Optional.empty();
            }

            String name;
            do {
                name = nameGen.get();

                if (name == null) {
                    return Optional.empty();
                }
            } while (!check.test(name));
            return Optional.of(name);
        };

        Remapper remapper = new BasicRemapper(scanner, new SafeMappingGenerator(generator));

        Path toPath = resolve(outJar);

        Files.deleteIfExists(toPath);

        HashMap<String, Object> properties = new HashMap<>();
        properties.put("create", "true");
        try (FileSystem out = FileSystems.newFileSystem(URI.create("jar:" + toPath.toUri()), properties)) {
            managed.getManagedClasses().forEach(c -> {
                ClassNode next = new ClassNode();
                ClassVisitor visitor = remapper.createClassVisitor(next);
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

    private Path resolve(String name) {
        return workingDir.resolve(name);
    }

    @SuppressWarnings("unchecked")
    private <T> Supplier<T> jsToSupplier(Object o) {
        if (o instanceof Undefined) {
            return null;
        }
        if (o instanceof ScriptFunction) {
            MethodHandle handle = ((ScriptFunction) o).getBoundInvokeHandle(null);
            return UnsafeSupplier.unsafe(() -> {
                Object ret = handle.invoke();
                if (ret instanceof CharSequence) {
                    ret = ret.toString();
                }
                return (T) ret;
            });
        }
        return (Supplier<T>) o;
    }
}
