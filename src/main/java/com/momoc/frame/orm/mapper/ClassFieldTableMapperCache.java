package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.AnnotationUtil;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ClassFieldTableMapperCache {
    /**
     * 类的映射缓存
     */
    static Map<Class<?>, HashMap<String, List<Method>>> TABLE_FIELD_NAME_SETTER_CACHE = new ConcurrentHashMap<>();

    /**
     * 构建表字段名称与setter方法关联关系
     *
     * @return
     */
    public static HashMap<String, List<Method>> buildFiledSetterMethodMap(Class<?> entityClass) {

        HashMap<String, List<Method>> tableFieldNameSetterMap = TABLE_FIELD_NAME_SETTER_CACHE.computeIfAbsent(entityClass, k -> new HashMap<>());

        if (!tableFieldNameSetterMap.isEmpty()) {
            return tableFieldNameSetterMap;
        }

        Field[] declaredFields = entityClass.getDeclaredFields();
        for (Field field : declaredFields) {
            field.setAccessible(true);
            String fieldName = field.getName();
            // 获取setter方法
            String setterName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            try {
                Method setterMethod = entityClass.getMethod(setterName, field.getType());
                String tableFieldName = AnnotationUtil.getFieldName(field);
                tableFieldNameSetterMap.computeIfAbsent(tableFieldName, k -> new ArrayList<>()).add(setterMethod);
            } catch (NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }


        return tableFieldNameSetterMap;

    }
}
