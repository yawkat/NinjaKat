package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Descriptor for a field (type + name).
 *
 * @author yawkat
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class FieldDescriptor extends Descriptor {
    private final NType type;

    public FieldDescriptor(String name, NType type) {
        super(name);
        this.type = type;
    }

    public static FieldDescriptor create(String name, String desc) {
        NType type = NType.fromAsm(org.objectweb.asm.Type.getType(desc));
        return new FieldDescriptor(name, type);
    }

    @Override
    public String toString() {
        return "F{" +
               type.getTypeName() +
               " " +
               getName() +
               "}";
    }
}
