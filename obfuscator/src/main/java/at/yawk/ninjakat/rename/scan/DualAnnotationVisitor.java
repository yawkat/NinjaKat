package at.yawk.ninjakat.rename.scan;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

/**
 * AnnotationVisitor that forwards events to two children.
 *
 * @author yawkat
 */
class DualAnnotationVisitor extends AnnotationVisitor {
    private final AnnotationVisitor a;
    private final AnnotationVisitor b;

    private DualAnnotationVisitor(AnnotationVisitor a, AnnotationVisitor b) {
        super(Opcodes.ASM5);
        this.a = a;
        this.b = b;
    }

    public static AnnotationVisitor create(AnnotationVisitor a, AnnotationVisitor b) {
        return a == null ?
                b :
                b == null ?
                        a :
                        new DualAnnotationVisitor(a, b);
    }

    @Override
    public void visit(String name, Object value) {
        a.visit(name, value);
        b.visit(name, value);
    }

    @Override
    public void visitEnum(String name, String desc, String value) {
        a.visitEnum(name, desc, value);
        b.visitEnum(name, desc, value);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String desc) {
        return create(a.visitAnnotation(name, desc), b.visitAnnotation(name, desc));
    }

    @Override
    public AnnotationVisitor visitArray(String name) {
        return create(a.visitArray(name), b.visitArray(name));
    }

    @Override
    public void visitEnd() {
        a.visitEnd();
        b.visitEnd();
    }
}
