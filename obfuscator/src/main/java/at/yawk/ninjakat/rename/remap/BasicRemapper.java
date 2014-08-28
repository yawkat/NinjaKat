package at.yawk.ninjakat.rename.remap;

import at.yawk.ninjakat.rename.descriptor.*;
import at.yawk.ninjakat.rename.path.ClassPathUtil;
import at.yawk.ninjakat.rename.scan.Scanner;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import java.util.*;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;

/**
 * Collision-detecting Remapper implementation.
 *
 * @author yawkat
 */
@Slf4j
public class BasicRemapper extends Remapper {
    /**
     * Scan result of the class path.
     */
    private final Scanner scanResult;
    /**
     * MappingGenerator implementation used to generate names.
     */
    private final MappingGenerator generator;
    /**
     * Generated names, mutable reference to map used by Remapper.
     */
    private final Map<Identifier, Identifier> mappings;

    /**
     * MultiMap of class members to "aliases" in subclasses (basically reverse of Scanner#aliases).
     */
    // original ident -> alias
    private final Multimap<MemberInfo, MemberInfo> aliases = HashMultimap.create();

    /**
     * Set of classes that were already mapped (only their class names).
     */
    private final Set<ClassDescriptor> mappedClasses = new HashSet<>();
    /**
     * Class names that are already used.
     */
    private final Set<ClassDescriptor> usedClassNames = new HashSet<>();

    private BasicRemapper(Scanner scanResult, MappingGenerator generator, Map<Identifier, Identifier> mappings) {
        super(mappings);
        this.scanResult = scanResult;
        this.generator = generator;
        this.mappings = mappings;

        build();
    }

    public BasicRemapper(Scanner scanResult, MappingGenerator generator) {
        this(scanResult, generator, new HashMap<>());
    }

    private void build() {
        // class mappings
        scanResult.getIdentifiers().forEach(im -> {
            Identifier i = im.getIdentifier();
            if (i instanceof ClassDescriptor) {
                buildClassMapping((ClassDescriptor) i, im.getMeta());
            }
        });

        // method & field (member) mappings
        scanResult.getIdentifiers().forEach(im -> {
            Identifier i = im.getIdentifier();
            if (i instanceof FieldInfo) {
                buildFieldMapping((FieldInfo) i, im.getMeta());
            } else if (i instanceof MethodInfo) {
                buildMethodMapping((MethodInfo) i, im.getMeta());
            }
        });
    }

    /**
     * Compute the conflictless mapping for a method and add it to the mapping map.
     */
    private void buildMethodMapping(MethodInfo mi, IdentifierMeta meta) {
        String name = generator.createMapping(mi, meta, s -> {
            MethodDescriptor desc = createMappedMethodDescriptor(mi, s);
            Optional<MethodInfo> conflict = ClassPathUtil.findMethod(scanResult.getPath(),
                                                                     mi.getOwner(),
                                                                     other -> map(other).getDescriptor().equals(desc));
            return !conflict.isPresent();
        }).orElseGet(() -> mi.getDescriptor().getName());
        MethodInfo mapped = new MethodInfo(
                map(mi.getOwner()),
                createMappedMethodDescriptor(mi, name)
        );
        putMemberMapping(mi, mapped);
    }

    /**
     * Create a mapped version of the given method info's descriptor with the given name and already mapped types.
     */
    private MethodDescriptor createMappedMethodDescriptor(MethodInfo mi, String name) {
        return new MethodDescriptor(
                name,
                map(mi.getDescriptor().getReturnType()),
                mi.getDescriptor()
                        .getParameterTypes()
                        .stream()
                        .map(this::map)
                        .collect(Collectors.toList())
        );
    }

    /**
     * Compute the conflictless mapping for a field and add it to the mapping map.
     */
    private void buildFieldMapping(FieldInfo fi, IdentifierMeta meta) {
        String name = generator.createMapping(fi, meta, s -> {
            FieldDescriptor desc = createMappedFieldDescriptor(fi, s);
            Optional<FieldInfo> conflict = ClassPathUtil.findField(scanResult.getPath(),
                                                                   fi.getOwner(),
                                                                   other -> map(other).getDescriptor().equals(desc));
            return !conflict.isPresent();
        }).orElseGet(() -> fi.getDescriptor().getName());
        FieldInfo mapped = new FieldInfo(
                map(fi.getOwner()),
                createMappedFieldDescriptor(fi, name)
        );
        putMemberMapping(fi, mapped);
    }

    /**
     * Create a mapped version of the given field info's descriptor with the given name and already mapped types.
     */
    private FieldDescriptor createMappedFieldDescriptor(FieldInfo fi, String name) {
        return new FieldDescriptor(name, map(fi.getDescriptor().getType()));
    }

    /**
     * Compute the name mapping of a class.
     */
    @SuppressWarnings("unchecked")
    private void buildClassMapping(ClassDescriptor i, IdentifierMeta meta) {
        if (!mappedClasses.add(i)) {
            // already mapped
            return;
        }

        // map supers first
        ClassPathUtil.supers(scanResult.getPath().findClass(i).get())
                .forEach(n -> {
                    ClassDescriptor parentDescriptor = new ClassDescriptor(n);
                    IdentifierMeta m = scanResult.getClassMeta().get(parentDescriptor);
                    if (m != null) { // in class path?
                        buildClassMapping(parentDescriptor, m);
                    }
                });

        // generate unique mapping
        generator.createMapping(i, meta, s -> !usedClassNames.contains(new ClassDescriptor(s))).ifPresent(name -> {
            usedClassNames.add(new ClassDescriptor(name));
            log.info("Adding mapping " + i + " -> " + name);
            mappings.put(i, new ClassDescriptor(name));
        });
    }

    /**
     * Add a method or field mapping.
     */
    private void putMemberMapping(MemberInfo from, MemberInfo to) {
        log.info("Adding mapping " + from + " -> " + to);
        mappings.put(from, to);
        mapAliases(from);
    }

    private void mapAliases(MemberInfo of) {
        MemberInfo mappedOf = map(of);
        aliases.get(of).forEach(alias -> {
            ClassDescriptor owner = map(alias.getOwner());
            MemberInfo mapped = mappedOf.withOwner(owner);
            putMemberMapping(alias, mapped);
        });
    }

}
