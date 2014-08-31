package at.yawk.ninjakat.renamelocal;

import at.yawk.ninjakat.rename.descriptor.FieldDescriptor;
import at.yawk.ninjakat.rename.descriptor.IdentifierMeta;
import at.yawk.ninjakat.rename.descriptor.MethodInfo;
import at.yawk.ninjakat.util.DualAnnotationVisitor;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.objectweb.asm.*;
import org.objectweb.asm.tree.AnnotationNode;

/**
 * @author yawkat
 */
class RenamingMethodVisitor extends MethodVisitor {
    private final LocalRenamer renamer;
    private final MethodInfo method;

    private List<AnnotationNode> annotations = null;

    public RenamingMethodVisitor(MethodVisitor mv, LocalRenamer renamer, MethodInfo method) {
        super(Opcodes.ASM5, mv);
        this.renamer = renamer;
        this.method = method;
    }

    @Override
    public AnnotationVisitor visitLocalVariableAnnotation(int typeRef, TypePath typePath, Label[] start, Label[] end,
                                                          int[] index, String desc, boolean visible) {
        AnnotationNode node = new AnnotationNode(desc);
        if (annotations == null) {
            annotations = new ArrayList<>();
        }
        annotations.add(node);
        return DualAnnotationVisitor.create(
                super.visitLocalVariableAnnotation(typeRef, typePath, start, end, index, desc, visible),
                node
        );
    }

    @Override
    public void visitLocalVariable(String name, String desc, String signature, Label start, Label end, int index) {
        Optional<String> mapping = renamer.createMapping(method, FieldDescriptor.create(name, desc),
                                                         new IdentifierMeta(annotations), s -> true);
        annotations = null;
        super.visitLocalVariable(mapping.orElse(name), desc, signature, start, end, index);
    }
}
