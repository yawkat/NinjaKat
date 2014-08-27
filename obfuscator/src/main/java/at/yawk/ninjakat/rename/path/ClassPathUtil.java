package at.yawk.ninjakat.rename.path;

import at.yawk.ninjakat.rename.descriptor.*;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * @author yawkat
 */
public class ClassPathUtil {
    @SuppressWarnings("unchecked")
    public static Stream<String> supers(ClassNode node) {
        if (node.superName == null) {
            return Stream.empty();
        }
        return Stream.concat(
                Stream.of(node.superName),
                ((List<String>) node.interfaces).stream()
        );
    }

    private static <T> Optional<T> find(ClassPath path, ClassNode in, Function<ClassNode, Optional<T>> get) {
        Optional<T> v = get.apply(in);
        if (v.isPresent()) {
            return v;
        }
        return supers(in).map(s -> {
            Optional<ClassNode> superClass = path.findClass(new ClassDescriptor(s));
            return superClass.<T>flatMap(c -> find(path, c, get));
        }).filter(Optional::isPresent).map(Optional::get).findFirst();
    }

    public static Optional<FieldInfo> findField(ClassPath path, ClassDescriptor clazz, FieldDescriptor descriptor) {
        return findField(path, clazz, fi -> fi.getDescriptor().equals(descriptor));
    }

    public static Optional<FieldInfo> findField(ClassPath path,
                                                ClassDescriptor clazz,
                                                Predicate<FieldInfo> condition) {
        return findField(path, clazz,
                         (cn, fn) -> condition.test(new FieldInfo(new ClassDescriptor(cn.name),
                                                                  FieldDescriptor.create(fn.name, fn.desc))));
    }

    @SuppressWarnings("unchecked")
    public static Optional<FieldInfo> findField(ClassPath path,
                                                ClassDescriptor clazz,
                                                BiPredicate<ClassNode, FieldNode> condition) {
        return find(path,
                    path.findClass(clazz).get(),
                    cn -> ((List<FieldNode>) cn.fields)
                            .stream()
                            .filter(fn -> condition.test(cn, fn))
                            .map(fn -> new FieldInfo(new ClassDescriptor(cn.name),
                                                     FieldDescriptor.create(fn.name, fn.desc)))
                            .findFirst());
    }

    public static Optional<MethodInfo> findMethod(ClassPath path, ClassDescriptor clazz, MethodDescriptor descriptor) {
        return findMethod(path, clazz, mi -> mi.getDescriptor().equals(descriptor));
    }

    public static Optional<MethodInfo> findMethod(ClassPath path,
                                                  ClassDescriptor clazz,
                                                  Predicate<MethodInfo> condition) {
        return findMethod(path, clazz,
                          (cn, mn) -> condition.test(new MethodInfo(new ClassDescriptor(cn.name),
                                                                    MethodDescriptor.create(mn.name, mn.desc))));
    }

    @SuppressWarnings("unchecked")
    private static Optional<MethodInfo> findMethod(ClassPath path,
                                                   ClassDescriptor clazz,
                                                   BiPredicate<ClassNode, MethodNode> condition) {
        return find(path,
                    path.findClass(clazz).get(),
                    cn -> ((List<MethodNode>) cn.methods)
                            .stream()
                            .filter(mn -> condition.test(cn, mn))
                            .map(mn -> new MethodInfo(new ClassDescriptor(cn.name),
                                                      MethodDescriptor.create(mn.name, mn.desc)))
                            .findFirst());
    }
}
