package at.yawk.ninjakat.renamelocal;

import at.yawk.ninjakat.rename.descriptor.FieldDescriptor;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface LocalMappingGenerator {
    Optional<String> createMapping(MethodInfo method, FieldDescriptor name, IdentifierMeta meta,
                                   Predicate<String> checker);
}
