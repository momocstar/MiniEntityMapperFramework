package com.momoc.frame.orm.asm;


import com.momoc.frame.orm.BaseEntityTemplateMapper;
import lombok.Getter;

import java.lang.reflect.Constructor;

public class EntityDynamicClassLoader extends ClassLoader {


    /**
     * 类加载器
     */
    @Getter
    private static ClassLoader ENTITY_CLASS_LOADER = new EntityDynamicClassLoader();

    public Class<?> defineClass(String name, byte[] b) {
        return defineClass(name, b, 0, b.length);
    }


    /**
     * 使用AMS动态生成模板查询类
     * @param entityClass 实体类与对应的数据库的表
     * @param idClass 主键类型
     * @return
     * @param <T> 实体
     * @param <E> 主键
     */
    public static <T, E> BaseEntityTemplateMapper<T, E> generateMapperTemplateClass(Class<T> entityClass, Class<E> idClass) {
        // 动态生成BaseEntityImpl类的字节码
        AsmClassInfo baseEntityImplClass = EntityDynamicClassCreator.createBaseEntityImplClass(entityClass, idClass);
        // 创建自定义类加载器实例
        EntityDynamicClassLoader classLoader = new EntityDynamicClassLoader();
        try {
            // 使用自定义类加载器加载字节码
            Class<?> dynamicClass = classLoader.defineClass(baseEntityImplClass.getClassFullName().replace("/", "."), baseEntityImplClass.getByteClassCode());
            Constructor<?> constructor = dynamicClass.getConstructor(Class.class, Class.class);
            // 实例化动态类
            Object instance = constructor.newInstance(entityClass, idClass);
            // 打印实例以验证
            return (BaseEntityTemplateMapper<T, E>) instance;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}