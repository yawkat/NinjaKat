package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.Identifier;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import java.util.Optional;
import java.util.function.Predicate;
import lombok.RequiredArgsConstructor;

/**
 * Forwarding MappingGenerator implementation.
 *
 * @author yawkat
 */
@RequiredArgsConstructor
public abstract class FilteringMappingGenerator implements MappingGenerator {
    private final MappingGenerator handle;

    @Override
    public Optional<String> createMapping(Identifier identifier, IdentifierMeta meta, Predicate<String> checker) {
        return handle.createMapping(identifier, meta, checker);
    }
}
