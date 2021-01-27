package com.dj.plugin.dot.lib;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.Attribute;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.TypePath;

public class DotMethodVisitor extends MethodVisitor {
    private String methodName;
    public DotMethodVisitor(MethodVisitor methodVisitor,String methodName) {
        super(Opcodes.ASM7, methodVisitor);
        this.methodName = methodName;
    }

    /**
     * 访问注解接口方法的默认值；
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotationDefault() {
        return super.visitAnnotationDefault();
    }

    /**
     * 访问方法的一个注解；
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitAnnotation(String descriptor, boolean visible) {
        System.err.println("     visitAnnotation注解："+descriptor);
        //判断，只有加了ASMNeed注解的方法，才插装
        if("Lcom/dj/asm/dot/annotation/DotAnnotation;".equals(descriptor)){
            return new DotAnnotationVisitor(mv.visitAnnotation(descriptor,visible));
        }
        return super.visitAnnotation(descriptor, visible);
    }

    /**
     * 访问方法签名上的一个类型的注解;
     * @param typeRef
     * @param typePath
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitTypeAnnotation(int typeRef, TypePath typePath, String descriptor, boolean visible) {
        return super.visitTypeAnnotation(typeRef, typePath, descriptor, visible);
    }

    /**
     * 访问注解参数数量，就是访问方法参数有注解参数个数;
     * @param parameterCount
     * @param visible
     */
    @Override
    public void visitAnnotableParameterCount(int parameterCount, boolean visible) {
        super.visitAnnotableParameterCount(parameterCount, visible);
    }

    /**
     * 访问参数的注解，返回一个AnnotationVisitor可以访问该注解值;
     * @param parameter
     * @param descriptor
     * @param visible
     * @return
     */
    @Override
    public AnnotationVisitor visitParameterAnnotation(int parameter, String descriptor, boolean visible) {
        return super.visitParameterAnnotation(parameter, descriptor, visible);
    }

    /**
     * 访问方法属性；
     * @param attribute
     */
    @Override
    public void visitAttribute(Attribute attribute) {
        super.visitAttribute(attribute);
    }

    /**
     * 开始访问方法代码，此处可以添加方法运行前拦截器;
     */
    @Override
    public void visitCode() {
        super.visitCode();
        System.err.println(("onClick".equals(methodName)));
        if("onClick".equals(methodName)){//点击事件
            mv.visitLdcInsn("TAG");
            mv.visitLdcInsn("---->" + methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
        if("onLongClick".equals(methodName)){//长按事件
            mv.visitLdcInsn("TAG");
            mv.visitLdcInsn("---->" + methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
        if("onCreate".equals(methodName) || "onResume".equals(methodName)){
            mv.visitLdcInsn("TAG");
            mv.visitLdcInsn("---->" + methodName);
            mv.visitMethodInsn(Opcodes.INVOKESTATIC, "android/util/Log", "e",
                    "(Ljava/lang/String;Ljava/lang/String;)I", false);
            mv.visitInsn(Opcodes.POP);
        }
    }

    /**
     * 访问方法局部变量的当前状态以及操作栈成员信息，方法栈必须是expanded 格式或者compressed格式,该方法必须在visitInsn方法前调用;
     * @param type
     * @param numLocal
     * @param local
     * @param numStack
     * @param stack
     */
    @Override
    public void visitFrame(int type, int numLocal, Object[] local, int numStack, Object[] stack) {
        super.visitFrame(type, numLocal, local, numStack, stack);
    }

    /**
     * 访问数值类型指令
     * @param opcode 表示操作码指令,在这里opcode可以是Opcodes.BIPUSH,Opcodes.SIPUSH,Opcodes.NEWARRAY中一个；
     * @param operand 表示操作数，
     *
     * 如果opcode 为BIPUSH,那么operand value必须在Byte. minValue和Byte.maxValue之间；
     * 如果opcode为SIPUSH,那么operand value必须在Short.minValue和Short.minValue之间；
     * 如果opcode为NEWARRAY,那么operand value 可以取下面中一个：
     * Opcodes.T_BOOLEN,OPcodes.T_BYTE,OPCODES.T_CHAR,OPcodes.T_SHORT,OPcodes.T_INT，OPcodes.T_FLOAT,Opcodes.T_DOUBLE,Opcodes.T_LONG；
     */
    @Override
    public void visitIntInsn(int opcode, int operand) {
        super.visitIntInsn(opcode, operand);
    }

    /**
     * 访问本地变量类型指令
     * @param opcode 操作码可以是LOAD,STORE，RET中一种；
     * @param var 表示需要访问的变量；
     */
    @Override
    public void visitVarInsn(int opcode, int var) {
        super.visitVarInsn(opcode, var);
    }

    /**
     * 访问类型指令，类型指令会把类的内部名称当成参数Type
     * @param opcode 操作码为NEW ,ANEWARRAY,CHECKCAST,INSTANCEOF;
     * @param type 对象或者数组的内部名称，可以通过Type.getInternalName()获取；
     */
    @Override
    public void visitTypeInsn(int opcode, String type) {
        super.visitTypeInsn(opcode, type);
    }

    /**
     * 域操作指令，用来加载或者存储对象的Field；
     * @param opcode
     * @param owner
     * @param name
     * @param descriptor
     */
    @Override
    public void visitFieldInsn(int opcode, String owner, String name, String descriptor) {
        super.visitFieldInsn(opcode, owner, name, descriptor);
    }

    /**
     * 访问方法操作指令
     * @param opcode 为INVOKESPECIAL,INVOKESTATIC,INVOKEVIRTUAL,INVOKEINTERFACE;
     * @param owner 方法拥有者的名称;
     * @param name 方法名称;
     * @param descriptor 方法描述，参数和返回值;
     * @param isInterface 是否是接口;
     */
    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
        System.err.println("      method name="+name+",owner="+owner+",descriptor="+descriptor);

    }
}
