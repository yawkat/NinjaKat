package at.yawk.ninjakat.rename.scan;

import at.yawk.ninjakat.rename.descriptor.ClassDescriptor;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import at.yawk.ninjakat.rename.descriptor.IdentifierWithMeta;
import at.yawk.ninjakat.rename.descriptor.MemberInfo;
import at.yawk.ninjakat.rename.path.ClassPath;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * @author yawkat
 */
@Slf4j
@Getter
@RequiredArgsConstructor
public class Scanner {
    private final ClassPath path;

    private final Set<IdentifierWithMeta> identifiers = new HashSet<>();
    private final Map<MemberInfo, MemberInfo> aliases = new HashMap<>();
    private final Map<ClassDescriptor, IdentifierMeta> classMeta = new HashMap<>();

    public void add(IdentifierWithMeta identifier) {
        log.info("Add:\t" + identifier);
        identifiers.add(identifier);
        if (identifier.getIdentifier() instanceof ClassDescriptor) {
            classMeta.put((ClassDescriptor) identifier.getIdentifier(), identifier.getMeta());
        }
    }

    public <Info extends MemberInfo> void addAlias(Info actual, Info alias) {
        aliases.put(alias, actual);
        log.info("Alias:\t" + alias + " -> " + actual);
    }
}
