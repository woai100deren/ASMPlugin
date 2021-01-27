package com.dj.plugin.dot.lib;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DotClassVisitor extends ClassVisitor {
    private String className;
    private String superName;

    public DotClassVisitor(ClassVisitor cv) {
        this(Opcodes.ASM7, cv);
    }
    public DotClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;
        System.err.println("className = "+className);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        System.err.println("methodName = "+name);
        return new DotMethodVisitor(methodVisitor,name);
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
    }
}
