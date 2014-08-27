package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

/**
 * Identifier for a method inside a class, including the descriptor for that class.
 *
 * @author yawkat
 */
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
public class MethodInfo extends MemberInfo {
    private final MethodDescriptor descriptor;

    public MethodInfo(ClassDescriptor owner, MethodDescriptor descriptor) {
        super(owner);
        this.descriptor = descriptor;
    }

    @Override
    public MethodInfo withOwner(ClassDescriptor owner) {
        return new MethodInfo(owner, descriptor);
    }
}
