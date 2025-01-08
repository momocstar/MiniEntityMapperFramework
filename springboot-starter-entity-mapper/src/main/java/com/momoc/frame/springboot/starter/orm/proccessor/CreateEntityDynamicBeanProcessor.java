package com.momoc.frame.springboot.starter.orm.proccessor;

import com.momoc.frame.orm.asm.EntityDynamicClassCreator;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class CreateEntityDynamicBeanProcessor implements BeanPostProcessor {

    @Override
    public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {

        //半成品对象
        Class<?> beanClass = bean.getClass();
        if (bean instanceof BaseEntityTemplateMapper) {

            Class<?> entityClass = null;
            Class<?> iDclass =  null;
            // 直接autowire注入
            Type genericSuperclass = beanClass.getGenericSuperclass();
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
            // 获取BaseEntityTemplateMapper的实际类型参数
            if (beanClass.getName().equalsIgnoreCase(BaseEntityTemplateMapper.class.getName())) {
                // 直接autowire注入
                return EntityDynamicClassCreator.createBaseEntityImplClass(entityClass, iDclass);
            }else{
                //通过继承注入,默认构造方法，创建半成品对象
                try {
                    Method method = beanClass.getMethod("buildMapperObj", Class.class, Class.class);
                    method.setAccessible(true);
                    method.invoke(bean, entityClass, iDclass);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return bean;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        return BeanPostProcessor.super.postProcessAfterInitialization(bean, beanName);
    }
}
