package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

/**
 * Identifier for a class member with a specific owning class.
 *
 * @author yawkat
 */
@Getter
@RequiredArgsConstructor
@ToString
@EqualsAndHashCode
public abstract class MemberInfo implements Identifier {
    private final ClassDescriptor owner;

    public abstract MemberInfo withOwner(ClassDescriptor owner);
}
