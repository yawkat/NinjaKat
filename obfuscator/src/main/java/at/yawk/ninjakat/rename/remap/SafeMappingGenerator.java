package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.Identifier;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.extern.slf4j.Slf4j;
import ninjakat.DoNotRename;
import org.objectweb.asm.Type;

/**
 * MappingGenerator instance that doesn't rename constructors or DoNotRename-annotated elements.
 *
 * @author yawkat
 */
@Slf4j
public class SafeMappingGenerator extends FilteringMappingGenerator {
    private static final String DO_NOT_RENAME = Type.getDescriptor(DoNotRename.class);

    public SafeMappingGenerator(MappingGenerator handle) {
        super(handle);
    }

    @Override
    public Optional<String> createMapping(Identifier identifier, IdentifierMeta meta, Predicate<String> checker) {
        if (identifier instanceof MethodInfo) {
            // (static) constructor
            if (((MethodInfo) identifier).getDescriptor().getName().charAt(0) == '<') {
                return Optional.empty();
            }
        }

        if (meta.getAnnotations().stream().anyMatch(annotation -> annotation.desc.equals(DO_NOT_RENAME))) {
            return Optional.empty();
        }

        return super.createMapping(identifier, meta, checker);
    }
}
