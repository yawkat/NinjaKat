package at.yawk.ninjakat.rename.descriptor;

/**
 * A field, variable, method return etc. type.
 *
 * @author yawkat
 */
public interface NType extends Identifier {
    /**
     * The class name of this type.
     */
    String getName();

    /**
     * The descriptor of this class, for example <code>Ljava.lang.Object;</code> or <code>Z</code>.
     */
    String getTypeName();

    public static NType fromAsm(org.objectweb.asm.Type type) {
        switch (type.getSort()) {
        case org.objectweb.asm.Type.BOOLEAN:
            return PrimitiveType.BOOLEAN;
        case org.objectweb.asm.Type.BYTE:
            return PrimitiveType.BYTE;
        case org.objectweb.asm.Type.SHORT:
            return PrimitiveType.SHORT;
        case org.objectweb.asm.Type.CHAR:
            return PrimitiveType.CHAR;
        case org.objectweb.asm.Type.INT:
            return PrimitiveType.INT;
        case org.objectweb.asm.Type.LONG:
            return PrimitiveType.LONG;
        case org.objectweb.asm.Type.FLOAT:
            return PrimitiveType.FLOAT;
        case org.objectweb.asm.Type.DOUBLE:
            return PrimitiveType.DOUBLE;
        case org.objectweb.asm.Type.VOID:
            return PrimitiveType.VOID;
        case org.objectweb.asm.Type.ARRAY:
            return new ArrayType(fromAsm(type.getElementType()));
        case org.objectweb.asm.Type.OBJECT:
            return new ClassDescriptor(type.getClassName());
        }
        throw new UnsupportedOperationException(String.valueOf(type.getSort()));
    }
}
