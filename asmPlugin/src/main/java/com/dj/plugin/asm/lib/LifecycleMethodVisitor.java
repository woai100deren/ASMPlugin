package com.dj.plugin.asm.lib;

import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleMethodVisitor extends MethodVisitor {
    private String className;
    private String methodName;
    public LifecycleMethodVisitor(MethodVisitor mv, String className, String methodName) {
        super(Opcodes.ASM7, mv);
        this.className = className;
        this.methodName = methodName;
    }
    @Override
    public void visitCode() {
        super.visitCode();
        System.out.println("==LifecycleMethodVisitor==visitCode==className:" + className + " ,methodName:" + methodName);
        mv.visitLdcInsn("TAG");
        mv.visitLdcInsn(className + "---->" + methodName);
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "i",
                "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
    }

    @Override
    public void visitInsn(int opcode) {
        if (opcode == Opcodes.RETURN) {
            insertCode();
        }
        super.visitInsn(opcode);
    }

    private void insertCode() {
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "com/dj/asm/plugin/MainActivity", "tag", "Z");
        Label label1 = new Label();
        mv.visitJumpInsn(Opcodes.IFEQ, label1);
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitLdcInsn("Tim");
        mv.visitLdcInsn("tag is ture  11111");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        Label label3 = new Label();
        mv.visitJumpInsn(Opcodes.GOTO, label3);
        mv.visitLabel(label1);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitLdcInsn("Tim");
        mv.visitLdcInsn("tag is false 22222");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e", "(Ljava/lang/String;Ljava/lang/String;)I", false);
        mv.visitInsn(Opcodes.POP);
        mv.visitLabel(label3);
        mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
        mv.visitInsn(Opcodes.RETURN);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
