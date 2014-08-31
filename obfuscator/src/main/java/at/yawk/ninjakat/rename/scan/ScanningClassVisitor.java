package at.yawk.ninjakat.rename.scan;

import at.yawk.ninjakat.rename.descriptor.*;
import at.yawk.ninjakat.util.DualAnnotationVisitor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * @author yawkat
 */
@Slf4j
class ScanningClassVisitor extends ClassVisitor {
    private final Scanner scanner;

    private ClassInfo clazz;

    // Hack: Mutable annotation list used in IdentifierMeta for this class
    private List<AnnotationNode> annotations = new ArrayList<>();

    public ScanningClassVisitor(ClassVisitor cv, Scanner scanner) {
        super(Opcodes.ASM5, cv);
        this.scanner = scanner;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        clazz = ClassInfo.create(name, superName, Arrays.asList(interfaces));
        scanner.add(new IdentifierWithMeta(clazz.getDescriptor(), new IdentifierMeta(annotations)));

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        log.debug("Annotation TYPE");
        return super.visitTypeAnnotation(typeRef, typePath, desc, visible);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        log.debug("Annotation");
        AnnotationNode node = new AnnotationNode(desc);
        annotations.add(node);
        return DualAnnotationVisitor.create(super.visitAnnotation(desc, visible), node);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodDescriptor method = MethodDescriptor.create(name, desc);

        MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
        return new ScanningMethodVisitor(parent, scanner, clazz, method);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldDescriptor field = FieldDescriptor.create(name, desc);

        FieldVisitor parent = super.visitField(access, name, desc, signature, value);
        return new ScanningFieldVisitor(parent, scanner, clazz, field);
    }

}
