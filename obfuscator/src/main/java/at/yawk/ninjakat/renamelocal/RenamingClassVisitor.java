package at.yawk.ninjakat.renamelocal;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import at.yawk.ninjakat.rename.descriptor.MethodDescriptor;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

/**
 * @author yawkat
 */
class RenamingClassVisitor extends ClassVisitor {
    private final LocalRenamer renamer;

    private ClassDescriptor descriptor;

    public RenamingClassVisitor(ClassVisitor cv, LocalRenamer renamer) {
        super(Opcodes.ASM5, cv);
        this.renamer = renamer;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        descriptor = new ClassDescriptor(name);
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        return new RenamingMethodVisitor(
                super.visitMethod(access, name, desc, signature, exceptions),
                renamer,
                new MethodInfo(descriptor, MethodDescriptor.create(name, desc))
        );
    }
}
