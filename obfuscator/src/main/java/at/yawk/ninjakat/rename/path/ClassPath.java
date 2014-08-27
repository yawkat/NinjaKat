package at.yawk.ninjakat.rename.path;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import java.util.Optional;
import java.util.stream.Stream;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.tree.ClassNode;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
public abstract class ClassPath implements AutoCloseable {
    private final Optional<ClassPath> parent;

    public Optional<ClassNode> findClass(ClassDescriptor descriptor) {
        return parent.flatMap(p -> p.findClass(descriptor));
    }

    public Stream<ClassNode> getClasses() {
        return parent.map(ClassPath::getClasses).orElse(Stream.empty());
    }

    @Override
    public void close() throws Exception {}
}
