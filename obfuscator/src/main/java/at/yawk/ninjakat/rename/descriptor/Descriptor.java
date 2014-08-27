package at.yawk.ninjakat.rename.descriptor;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

/**
 * A descriptor that can be used to uniquely identify something in a class. There may not be two members with the same
 * descriptor in a class tree.
 *
 * @author yawkat
 */
@Getter
@RequiredArgsConstructor
@EqualsAndHashCode
public abstract class Descriptor {
    private final String name;
}
