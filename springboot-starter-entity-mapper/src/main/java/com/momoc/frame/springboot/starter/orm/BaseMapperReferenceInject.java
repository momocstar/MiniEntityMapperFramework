package com.momoc.frame.springboot.starter.orm;

import com.momoc.frame.orm.asm.EntityDynamicClassCreator;
import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.springboot.starter.orm.annotation.EntityMapperAutowired;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * @author momoc
 * @version 1.0
 * className MRpcReferenceInject
 * description
 * date 2025/01/08 10:51 上午
 */
@Slf4j
@Component
public class BaseMapperReferenceInject implements BeanPostProcessor {


    private static final Map<Class<?>, Map<Class<?>, BaseEntityTemplateMapper<?,?>>>  CACHE_CLASS = new HashMap<>();


    @Autowired
    private ApplicationContext applicationContext;

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
        return bean;
    }


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class aClass = bean.getClass();

        for (Field declaredField : aClass.getDeclaredFields()) {
            EntityMapperAutowired annotation = declaredField.getAnnotation(EntityMapperAutowired.class);
            if (annotation == null) {
                return bean;
            }
            Class<?> entityClass = null;
            Class<?> iDclass = null;
            // 直接autowire注入
            Type genericSuperclass = declaredField.getGenericType();
            if (genericSuperclass instanceof ParameterizedType) {
                ParameterizedType paramType = (ParameterizedType) genericSuperclass;
                Type[] typeArguments = paramType.getActualTypeArguments();
                // 假设你知道泛型参数的位置，例如第一个是ID类型，第二个是其他类型
                entityClass = (Class<?>) typeArguments[0];
                iDclass = (Class<?>) typeArguments[1];
            }
            if (entityClass == null || iDclass == null) {
                throw new RuntimeException("BaseEntityTemplateMapper did not provide corresponding generic and primary key types");
            }


            Map<Class<?>, BaseEntityTemplateMapper<?, ?>> classBaseEntityTemplateMapperMap = CACHE_CLASS.computeIfAbsent(entityClass, k -> new HashMap<>());


            try {
                BaseEntityTemplateMapper<?, ?> baseEntityTemplateMapper;
                if (classBaseEntityTemplateMapperMap.containsKey(iDclass)) {
                    baseEntityTemplateMapper = classBaseEntityTemplateMapperMap.get(iDclass);
                }else{
                    baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(entityClass, iDclass);
                    classBaseEntityTemplateMapperMap.put(iDclass, baseEntityTemplateMapper);
                    CACHE_CLASS.put(entityClass, classBaseEntityTemplateMapperMap);
                }
                declaredField.setAccessible(true);
                declaredField.set(bean, baseEntityTemplateMapper);

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        return bean;
    }
}
