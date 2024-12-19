package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;

import java.lang.reflect.Field;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SelectSqlFieldGenerate {


    static Map<Class<?>, String> CACHE_CLASS_SELECT_SQL_MAP = new ConcurrentHashMap<>();


    /**
     * 生成此实体类的查询sql
     * @param entityClass
     * @return
     */
    public static String getAllTableQueryField(Class<?> entityClass) {

        if (CACHE_CLASS_SELECT_SQL_MAP.containsKey(entityClass)) {
            return CACHE_CLASS_SELECT_SQL_MAP.get(entityClass);
        }

        StringBuilder sb = new StringBuilder();
        sb.append("select  ");

        for (Field declaredField : entityClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
            String name;
            if (annotation != null) {
                name = annotation.name();
            } else {
                name = declaredField.getName();
            }
            if (sb.indexOf(name) == -1) {
                sb.append(name).append(",");
            }
        }
        sb.deleteCharAt(sb.length() - 1);
        sb.append(" from ").append(getTableName(entityClass));
        String selectSql = sb.toString();
        CACHE_CLASS_SELECT_SQL_MAP.put(entityClass, selectSql);
        return selectSql;

    }

    /**
     * 获取表名
     *
     * @return
     */
    public static String getTableName(Class<?> entityClass) {
        // 检查实体类是否有MiniEntityTableName注解
        MiniEntityTableName annotation = entityClass.getAnnotation(MiniEntityTableName.class);
        if (annotation != null) {
            // 如果有注解，返回注解中的表名
            return annotation.name();
        } else {
            // 如果没有注解，返回默认的表名（例如，使用类名作为表名）
            return entityClass.getSimpleName();
        }
    }
}
