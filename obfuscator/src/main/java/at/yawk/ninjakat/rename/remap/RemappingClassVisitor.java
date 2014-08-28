package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.*;
import org.objectweb.asm.*;

/**
 * @author yawkat
 */
class RemappingClassVisitor extends ClassVisitor {
    private final Remapper remapper;

    private ClassDescriptor clazz;

    public RemappingClassVisitor(ClassVisitor cv, Remapper remapper) {
        super(Opcodes.ASM5, cv);
        this.remapper = remapper;
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        clazz = new ClassDescriptor(name);
        name = remapper.map(clazz).getName();
        superName = remapper.map(new ClassDescriptor(superName)).getName();
        for (int i = 0; i < interfaces.length; i++) {
            interfaces[i] = remapper.map(new ClassDescriptor(interfaces[i])).getName();
        }

        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodInfo method = new MethodInfo(clazz, MethodDescriptor.create(name, desc));
        MethodInfo mapped = remapper.map(method);
        for (int i = 0; i < exceptions.length; i++) {
            exceptions[i] = remapper.map(new ClassDescriptor(exceptions[i])).getName();
        }

        name = mapped.getDescriptor().getName();
        desc = mapped.getDescriptor().getDesc();

        MethodVisitor parent = super.visitMethod(access, name, desc, signature, exceptions);
        return new RemappingMethodVisitor(parent, remapper, method);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        FieldInfo field = new FieldInfo(clazz, FieldDescriptor.create(name, desc));
        FieldInfo mapped = remapper.map(field);

        name = mapped.getDescriptor().getName();
        desc = mapped.getDescriptor().getType().getTypeName();

        FieldVisitor parent = super.visitField(access, name, desc, signature, value);
        return new RemappingFieldVisitor(parent, remapper, field);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor parent = super.visitAnnotation(remapper.map(new ClassDescriptor(desc)).getName(),
                                                         visible);
        return new RemappingAnnotationVisitor(parent, remapper);
    }

    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor parent = super.visitTypeAnnotation(typeRef,
                                                             typePath,
                                                             remapper.map(new ClassDescriptor(desc)).getName(),
                                                             visible);
        return new RemappingAnnotationVisitor(parent, remapper);
    }
}
