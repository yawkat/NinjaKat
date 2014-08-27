package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import at.yawk.ninjakat.rename.descriptor.FieldInfo;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author yawkat
 */
public class RemappingFieldVisitor extends FieldVisitor {
    private final Remapper remapper;

    private final FieldInfo field;

    public RemappingFieldVisitor(FieldVisitor fv, Remapper remapper, FieldInfo field) {
        super(Opcodes.ASM5, fv);
        this.remapper = remapper;
        this.field = field;
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor parent = super.visitAnnotation(remapper.map(new ClassDescriptor(desc)).getName(),
                                                         visible);
        return new RemappingAnnotationVisitor(parent, remapper);
    }
}
