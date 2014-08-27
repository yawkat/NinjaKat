package at.yawk.ninjakat.rename.descriptor;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import lombok.EqualsAndHashCode;
import lombok.Getter;

/**
 * Descriptor for a method (return + name + argument types).
 *
 * @author yawkat
 */
@Getter
@EqualsAndHashCode(callSuper = true)
public class MethodDescriptor extends Descriptor {
    private final NType returnType;
    private final List<NType> parameterTypes;

    public MethodDescriptor(String name, NType returnType, List<NType> parameterTypes) {
        super(name);
        this.returnType = returnType;
        this.parameterTypes = parameterTypes;
    }

    public static MethodDescriptor create(String name, String desc) {
        NType returnType = NType.fromAsm(org.objectweb.asm.Type.getReturnType(desc));
        List<NType> parameterTypes = Arrays
                .stream(org.objectweb.asm.Type.getArgumentTypes(desc))
                .map(NType::fromAsm)
                .collect(Collectors.toList());
        return new MethodDescriptor(name, returnType, parameterTypes);
    }

    @Override
    public String toString() {
        return "M{" +
               getName() +
               "(" + parameterTypes.stream().map(NType::getTypeName).collect(Util.toStringCollector()) + ")" +
               returnType.getTypeName()
               + "}";
    }

    public String getDesc() {
        return org.objectweb.asm.Type.getMethodDescriptor(
                org.objectweb.asm.Type.getType(returnType.getTypeName()),
                parameterTypes.stream()
                        .map(NType::getTypeName)
                        .map(org.objectweb.asm.Type::getType)
                        .toArray(org.objectweb.asm.Type[]::new)
        );
    }
}
