package at.yawk.ninjakat.rename.descriptor;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Value;

/**
 * @author yawkat
 */
@Value
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class PrimitiveType implements NType {
    public static final PrimitiveType BOOLEAN = new PrimitiveType("boolean", "Z");
    public static final PrimitiveType BYTE = new PrimitiveType("byte", "B");
    public static final PrimitiveType SHORT = new PrimitiveType("short", "S");
    public static final PrimitiveType CHAR = new PrimitiveType("char", "C");
    public static final PrimitiveType INT = new PrimitiveType("int", "I");
    public static final PrimitiveType LONG = new PrimitiveType("long", "J");
    public static final PrimitiveType FLOAT = new PrimitiveType("float", "F");
    public static final PrimitiveType DOUBLE = new PrimitiveType("double", "D");
    public static final PrimitiveType VOID = new PrimitiveType("void", "V");

    String name;
    String typeName;
}
