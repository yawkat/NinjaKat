package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.Identifier;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * @author yawkat
 */
public interface MappingGenerator {
    Optional<String> createMapping(Identifier identifier, IdentifierMeta meta, Predicate<String> checker);
}
