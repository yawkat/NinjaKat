package at.yawk.ninjakat.rename.remap;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author yawkat
 */
public class RemappingAnnotationVisitor extends AnnotationVisitor {
    private final Remapper remapper;

    public RemappingAnnotationVisitor(AnnotationVisitor av, Remapper remapper) {
        super(Opcodes.ASM5, av);
        this.remapper = remapper;
    }
}
