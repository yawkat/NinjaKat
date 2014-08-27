package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Identifier for a field inside a class, including the descriptor for that class.
 *
 * @author yawkat
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class FieldInfo extends MemberInfo  {
    private final FieldDescriptor descriptor;

    public FieldInfo(ClassDescriptor owner, FieldDescriptor descriptor) {
        super(owner);
        this.descriptor = descriptor;
    }

    @Override
    public FieldInfo withOwner(ClassDescriptor owner) {
        return new FieldInfo(owner, descriptor);
    }
}
