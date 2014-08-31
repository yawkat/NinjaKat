package at.yawk.ninjakat.renamelocal;

import at.yawk.ninjakat.rename.descriptor.FieldDescriptor;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;
import org.objectweb.asm.ClassVisitor;

/**
 * @author yawkat
 */
@RequiredArgsConstructor
public class LocalRenamer {
    private final LocalMappingGenerator generator;

    Optional<String> createMapping(MethodInfo method, FieldDescriptor name, IdentifierMeta meta,
                                   Predicate<String> checker) {
        return generator.createMapping(method, name, meta, checker);
    }

    public ClassVisitor createClassVisitor(ClassVisitor parent) {
        return new RenamingClassVisitor(parent, this);
    }
}
