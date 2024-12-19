package com.momoc.frame.orm.asm;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;

import java.util.UUID;

import static org.objectweb.asm.Opcodes.*;

/**
 * 通过ASM生成动态模板类
 */
public class EntityDynamicClassCreator {

    /**
     * 生成的包名称
     */
    private final static String PACKAGE_NAME = "com/momoc/frame/orm/mapper/generate";

    /**
     * 生成的类名称： Entity实体类名：UUID,8位
     */
    private static String CLASS_NAME = "MiniEntityMapperEnhancer%sAms$%s";

    /**
     * 继承的父类
     */
    private static String SUPER_CLASS_NAME = Type.getInternalName(BaseEntityTemplateMapper.class);

    private static String getUUID(){
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8);
    }

    public static AsmClassInfo createBaseEntityImplClass(Class<?> entityClass, Class<?> idClass) {
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS | ClassWriter.COMPUTE_FRAMES);
        MethodVisitor mv;

        String simpleClassName = String.format(CLASS_NAME, idClass.getSimpleName(), getUUID());
        //带有/
        String classFullName = PACKAGE_NAME + "/" + simpleClassName;

        String entityClassName = Type.getInternalName(entityClass);
        String idClassDescription = Type.getDescriptor(idClass);
        //泛型签名
        String genericSignature = String.format("L%s<L%s;%s>;",
                SUPER_CLASS_NAME, entityClassName, idClassDescription);
        // 访问标志和父类名称
        cw.visit(V1_8, ACC_PUBLIC + ACC_SUPER, classFullName.replace('.', '/'), genericSignature, SUPER_CLASS_NAME, null);

        // 定义构造函数
        {
            String constructorDescriptor = Type.getMethodDescriptor(Type.VOID_TYPE, Type.getType(Class.class), Type.getType(Class.class));
            String paramsDescriptor = String.format("(Ljava/lang/Class<L%s;>;Ljava/lang/Class<%s>;)V", entityClassName, idClassDescription);

            mv = cw.visitMethod(ACC_PUBLIC, "<init>", constructorDescriptor, paramsDescriptor, null);
            mv.visitCode();

            // 加载this，entityClass和idClass参数
            mv.visitVarInsn(ALOAD, 0);
            mv.visitVarInsn(ALOAD, 1);
            mv.visitVarInsn(ALOAD, 2);

            // 调用父类的构造方法
            mv.visitMethodInsn(INVOKESPECIAL, SUPER_CLASS_NAME, "<init>", constructorDescriptor, false);

            // 返回
            mv.visitInsn(RETURN);

            // 完成方法
            mv.visitMaxs(3, 4);
            mv.visitEnd();
        }

        // 完成类的创建
        cw.visitEnd();
        return new AsmClassInfo(cw.toByteArray(), classFullName, PACKAGE_NAME, simpleClassName);
    }


}