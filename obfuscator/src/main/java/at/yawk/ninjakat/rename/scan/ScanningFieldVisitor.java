package at.yawk.ninjakat.rename.scan;

import at.yawk.ninjakat.rename.descriptor.*;
import java.util.ArrayList;
import java.util.List;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * @author yawkat
 */
public class ScanningFieldVisitor extends FieldVisitor {
    private final Scanner scanner;

    private final ClassInfo clazz;
    private final FieldDescriptor field;

    // Hack: Mutable annotation list used in IdentifierMeta for this method
    private List<AnnotationNode> annotations = new ArrayList<>();

    public ScanningFieldVisitor(FieldVisitor fv, Scanner scanner, ClassInfo clazz, FieldDescriptor field) {
        super(Opcodes.ASM5, fv);
        this.scanner = scanner;
        this.clazz = clazz;
        this.field = field;

        scanner.add(new IdentifierWithMeta(new FieldInfo(clazz.getDescriptor(), field),
                                           new IdentifierMeta(annotations)));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationNode node = new AnnotationNode(desc);
        annotations.add(node);
        return DualAnnotationVisitor.create(super.visitAnnotation(desc, visible), node);
    }
}
