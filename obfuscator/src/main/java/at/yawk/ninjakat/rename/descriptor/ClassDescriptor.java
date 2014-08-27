package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;

/**
 * Unique descriptor for a class.
 *
 * @author yawkat
 */
@EqualsAndHashCode(callSuper = true)
public class ClassDescriptor extends Descriptor implements NType {
    public ClassDescriptor(String name) {
        super(name.replace('.', '/'));
    }

    @Override
    public String getTypeName() {
        return "L" + getName() + ";";
    }

    @Override
    public String toString() {
        return "C{" + getTypeName() + "}";
    }
}
