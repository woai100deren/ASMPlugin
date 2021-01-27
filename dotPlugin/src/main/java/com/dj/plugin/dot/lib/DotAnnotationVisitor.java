package com.dj.plugin.dot.lib;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Opcodes;

public class DotAnnotationVisitor extends AnnotationVisitor {
    public DotAnnotationVisitor(AnnotationVisitor annotationVisitor){
        this(Opcodes.ASM7, annotationVisitor);
    }
    public DotAnnotationVisitor(int api, AnnotationVisitor annotationVisitor) {
        super(api, annotationVisitor);
    }

    @Override
    public void visit(String name, Object value) {
        super.visit(name, value);
        System.err.println("DotAnnotationVisitor name："+name+",value:"+value.toString());
    }

    @Override
    public AnnotationVisitor visitAnnotation(String name, String descriptor) {
        return super.visitAnnotation(name, descriptor);
    }
}
