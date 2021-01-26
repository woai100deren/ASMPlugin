package com.dj.plugin.asm.lib;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LifecycleClassVisitor extends ClassVisitor {
    private String className;
    private String superName;

    public LifecycleClassVisitor(ClassVisitor cv) {
        this(Opcodes.ASM7, cv);
    }
    public LifecycleClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.className = name;
        this.superName = superName;
        System.out.println("--LifecycleClassVisitor--visit--className:" + className + " ,superName:" + superName);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        System.out.println("--LifecycleClassVisitor--visitMethod--methodName:" + name + " ,desc:" + desc);
        MethodVisitor methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions);
        if (superName.equals("androidx/appcompat/app/AppCompatActivity")) {
            if (name.startsWith("onCreate")) {
                return new LifecycleMethodVisitor(methodVisitor,className,name);
            }
        }
        return methodVisitor;
    }

    @Override
    public void visitEnd() {
        super.visitEnd();
        System.out.println("--LifecycleClassVisitor--visitEnd--className:" + className + " ,superName:" + superName);
    }
}
