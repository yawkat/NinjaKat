package at.yawk.ninjakat.rename.descriptor;

import lombok.Value;

/**
 * Identifier + IdentifierMeta tuple.
 *
 * @author yawkat
 */
@Value
public class IdentifierWithMeta {
    private final Identifier identifier;
    private final IdentifierMeta meta;
}
