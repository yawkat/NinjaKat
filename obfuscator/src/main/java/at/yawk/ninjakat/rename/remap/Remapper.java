package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.ArrayType;
import at.yawk.ninjakat.rename.descriptor.Identifier;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.ClassVisitor;

/**
 * @author yawkat
 */
@Slf4j
@RequiredArgsConstructor
public class Remapper {
    private final Map<Identifier, Identifier> remaps;

    @SuppressWarnings("unchecked")
    public <D extends Identifier> D map(D original) {
        if (original instanceof ArrayType) {
            return (D) new ArrayType(map(((ArrayType) original).getComponentType()));
        }
        log.info("Map " + original + " -> " + remaps.get(original));
        return (D) remaps.getOrDefault(original, original);
    }

    public ClassVisitor createClassVisitor(ClassVisitor parent) {
        return new RemappingClassVisitor(parent, this);
    }
}
