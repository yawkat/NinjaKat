package at.yawk.ninjakat.rename.descriptor;

import com.google.common.collect.ImmutableList;
import java.util.List;
import lombok.Value;
import org.objectweb.asm.tree.AnnotationNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.FieldNode;
import org.objectweb.asm.tree.MethodNode;

/**
 * Meta information for an Identifier that isn't used for identification but is still used for computation.
 *
 * @author yawkat
 */
@Value
public class IdentifierMeta {
    private final List<AnnotationNode> annotations;

    public static IdentifierMeta create(List<AnnotationNode> invisibleAnnotations,
                                        List<AnnotationNode> visibleAnnotations) {
        return new IdentifierMeta(ImmutableList.<AnnotationNode>builder()
                                          .addAll(invisibleAnnotations)
                                          .addAll(visibleAnnotations)
                                          .build());
    }

    @SuppressWarnings("unchecked")
    public static IdentifierMeta create(MethodNode method) {
        return create(method.invisibleAnnotations, method.visibleAnnotations);
    }

    @SuppressWarnings("unchecked")
    public static IdentifierMeta create(FieldNode field) {
        return create(field.invisibleAnnotations, field.visibleAnnotations);
    }

    @SuppressWarnings("unchecked")
    public static IdentifierMeta create(ClassNode clazz) {
        return create(clazz.invisibleAnnotations, clazz.visibleAnnotations);
    }
}
