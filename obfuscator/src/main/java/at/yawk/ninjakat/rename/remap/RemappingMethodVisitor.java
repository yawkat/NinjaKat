package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.*;
import lombok.extern.slf4j.Slf4j;
import org.objectweb.asm.*;

/**
 * @author yawkat
 */
@Slf4j
class RemappingMethodVisitor extends MethodVisitor {
    private final Remapper remapper;

    private final MethodInfo method;

    public RemappingMethodVisitor(MethodVisitor mv, Remapper remapper, MethodInfo method) {
        super(Opcodes.ASM5, mv);
        this.remapper = remapper;
        this.method = method;
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
        switch (opcode) {
        case Opcodes.INVOKESTATIC:
        case Opcodes.INVOKEVIRTUAL:
        case Opcodes.INVOKEINTERFACE:
        case Opcodes.INVOKESPECIAL:
            MethodDescriptor call = MethodDescriptor.create(name, desc);
            MethodInfo remapped = remapper.map(new MethodInfo(new ClassDescriptor(owner), call));
            name = remapped.getDescriptor().getName();
            desc = remapped.getDescriptor().getDesc();
            owner = remapped.getOwner().getName();
            break;
        default:
            log.warn("Unknown MethodInsn opcode " + opcode);
            break;
        }

        super.visitMethodInsn(opcode, owner, name, desc, itf);
    }

    @Override
    public void visitInvokeDynamicInsn(String name, String desc, Handle bsm, Object... bsmArgs) {
        String interfaceName = Type.getReturnType(desc).getInternalName();
        String methodDesc = bsmArgs[0].toString();

        MethodDescriptor descriptor = MethodDescriptor.create(name, methodDesc);
        MethodInfo info = new MethodInfo(new ClassDescriptor(interfaceName), descriptor);

        for (int i = 0; i < bsmArgs.length; i++) {
            if (bsmArgs[i] instanceof Handle) {
                bsmArgs[i] = mapHandle((Handle) bsmArgs[i]);
            } else if (bsmArgs[i] instanceof Type) {
                Type type = (Type) bsmArgs[i];
                int sort = type.getSort();
                if (sort == Type.METHOD) {
                    Type[] argumentTypes = type.getArgumentTypes();
                    for (int j = 0; j < argumentTypes.length; j++) {
                        argumentTypes[j] = mapType(argumentTypes[j]);
                    }
                    Type returnType = mapType(type.getReturnType());

                    bsmArgs[i] = Type.getMethodType(returnType, argumentTypes);
                } else {
                    bsmArgs[i] = mapType(type);
                }
            } else {
                log.warn("Unknown BSM type " + bsmArgs[i].getClass());
            }
        }

        MethodInfo mapped = remapper.map(info);
        name = mapped.getDescriptor().getName();
        desc = Type.getMethodDescriptor(Type.getType(mapped.getOwner().getTypeName()), Type.getArgumentTypes(desc));
        bsm = mapHandle(bsm);

        super.visitInvokeDynamicInsn(name, desc, bsm, bsmArgs);
    }

    private Type mapType(Type type) {
        return Type.getType(remapper.map(NType.fromAsm(type)).getTypeName());
    }

    private Handle mapHandle(Handle handle) {
        String owner = handle.getOwner();
        String name = handle.getName();
        String desc = handle.getDesc();
        MethodInfo mapped =
                remapper.map(new MethodInfo(new ClassDescriptor(owner), MethodDescriptor.create(name, desc)));

        owner = mapped.getOwner().getName();
        name = mapped.getDescriptor().getName();
        desc = mapped.getDescriptor().getDesc();

        return new Handle(handle.getTag(), owner, name, desc);
    }

    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String desc) {
        switch (opcode) {
        case Opcodes.GETFIELD:
        case Opcodes.PUTFIELD:
        case Opcodes.GETSTATIC:
        case Opcodes.PUTSTATIC:
            FieldDescriptor fi = FieldDescriptor.create(name, desc);
            FieldInfo remapped = remapper.map(new FieldInfo(new ClassDescriptor(owner), fi));
            name = remapped.getDescriptor().getName();
            desc = remapped.getDescriptor().getType().getTypeName();
            owner = remapped.getOwner().getName();
            break;
        default:
            log.warn("Unknown FieldInsn opcode " + opcode);
            break;
        }

        super.visitFieldInsn(opcode, owner, name, desc);
    }

    @Override
    public AnnotationVisitor visitInsnAnnotation(int typeRef, TypePath typePath, String desc, boolean visible) {
        AnnotationVisitor parent = super.visitInsnAnnotation(typeRef,
                                                             typePath,
                                                             remapper.map(new ClassDescriptor(desc)).getName(),
                                                             visible);
        return new RemappingAnnotationVisitor(parent, remapper);
    }

    @Override
    public AnnotationVisitor visitAnnotation(String desc, boolean visible) {
        AnnotationVisitor parent = super.visitAnnotation(remapper.map(new ClassDescriptor(desc)).getName(),
                                                         visible);
        return new RemappingAnnotationVisitor(parent, remapper);
    }

    @Override
    public void visitTypeInsn(int opcode, String type) {
        type = remapper.map(NType.fromAsm(Type.getObjectType(type))).getName();
        super.visitTypeInsn(opcode, type);
    }
}
