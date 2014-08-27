package at.yawk.ninjakat.rename.descriptor;

import lombok.Value;

/**
 * NType implementation for arrays.
 *
 * @author yawkat
 */
@Value
public class ArrayType implements NType {
    private final NType componentType;

    @Override
    public String getName() {
        return "[" + componentType.getName();
    }

    @Override
    public String getTypeName() {
        return "[" + componentType.getTypeName();
    }
}
