package at.yawk.ninjakat.rename.descriptor;

import java.util.List;
import java.util.stream.Collectors;
import lombok.Value;

/**
 * Context information for a class (name + superclasses).
 *
 * @author yawkat
 */
@Value
public class ClassInfo {
    private final ClassDescriptor descriptor;
    private final ClassDescriptor superclass;
    private final List<ClassDescriptor> interfaces;

    public static ClassInfo create(String name, String superName, List<String> interfaces) {
        ClassDescriptor descriptor = new ClassDescriptor(name);
        ClassDescriptor superclass = new ClassDescriptor(superName);
        List<ClassDescriptor> interfaceDescriptors = interfaces.stream().map(ClassDescriptor::new)
                .collect(Collectors.toList());
        return new ClassInfo(descriptor, superclass, interfaceDescriptors);
    }
}
