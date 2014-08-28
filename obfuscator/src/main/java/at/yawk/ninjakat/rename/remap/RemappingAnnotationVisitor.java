package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author yawkat
 */
class RemappingAnnotationVisitor extends AnnotationVisitor {
    private final Remapper remapper;

    public RemappingAnnotationVisitor(AnnotationVisitor av, Remapper remapper) {
        super(Opcodes.ASM5, av);
        this.remapper = remapper;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        // TODO name?
        desc = remapper.map(new ClassDescriptor(desc)).getName();

        return new RemappingAnnotationVisitor(super.visitAnnotation(name, desc), remapper);
    }
}
