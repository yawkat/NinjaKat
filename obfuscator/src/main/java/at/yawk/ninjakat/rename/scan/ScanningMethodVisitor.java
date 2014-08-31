package at.yawk.ninjakat.rename.scan;

import at.yawk.ninjakat.rename.descriptor.*;
import at.yawk.ninjakat.rename.path.ClassPathUtil;
import at.yawk.ninjakat.util.DualAnnotationVisitor;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * @author yawkat
 */
@Slf4j
class ScanningMethodVisitor extends MethodVisitor {
    private final Scanner scanner;

    private final ClassInfo clazz;
    private final MethodDescriptor method;

    // Hack: Mutable annotation list used in IdentifierMeta for this method
    private List<AnnotationNode> annotations = new ArrayList<>();

    public ScanningMethodVisitor(MethodVisitor mv, Scanner scanner, ClassInfo clazz, MethodDescriptor method) {
        super(Opcodes.ASM5, mv);
        this.scanner = scanner;
        this.clazz = clazz;
        this.method = method;

        scanner.add(new IdentifierWithMeta(new MethodInfo(clazz.getDescriptor(), method),
                                           new IdentifierMeta(annotations)));
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationNode node = new AnnotationNode(desc);
        annotations.add(node);
        return DualAnnotationVisitor.create(super.visitAnnotation(desc, visible), node);
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        switch (opcode) {
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKEINTERFACE:
            MethodDescriptor call = MethodDescriptor.create(name, desc);
            ClassPathUtil.findMethod(scanner.getPath(), clazz.getDescriptor(), call)
                    .ifPresent(actual -> scanner.addAlias(actual, new MethodInfo(new ClassDescriptor(owner), call)));
            break;
        case Opcodes.INVOKESPECIAL: // private / construct
            break;
        default:
            log.warn("Unknown MethodInsn opcode " + opcode);
            break;
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        switch (opcode) {
        case Opcodes.GETFIELD:
        case Opcodes.PUTFIELD:
        case Opcodes.GETSTATIC:
        case Opcodes.PUTSTATIC:
            FieldDescriptor fi = FieldDescriptor.create(name, desc);
            ClassPathUtil.findField(scanner.getPath(), clazz.getDescriptor(), fi)
                    .ifPresent(actual -> scanner.addAlias(actual, new FieldInfo(new ClassDescriptor(owner), fi)));
            break;
        default:
            log.warn("Unknown FieldInsn opcode " + opcode);
            break;
        }

        super.visitFieldInsn(opcode, owner, name, desc);
    }
}
