package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.EntityID;
import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SQLFieldGenerate {


    static Map<Class<?>, String> CACHE_CLASS_SELECT_SQL_MAP = new ConcurrentHashMap<>();


    /**
     * 生成此实体类的查询sql
     *
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


    public static StringBuilder generaInsertSQL(String tableName, DBParam... dbParams) {
        StringBuilder sql = new StringBuilder();
        sql.append("insert into ").append(tableName).append("(");
        StringBuilder values = new StringBuilder("values(");

        for (DBParam dbParam : dbParams) {
            sql.append(dbParam.getName()).append(",");
            values.append("@").append(dbParam.getName()).append(",");
        }
        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(") ");
        return sql.append(values);
    }


    public static <Entity> StringBuilder generaEntityInsertSQL(String tableName, Entity entity) {

        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");

        StringBuilder values = new StringBuilder("values(");
        Class<?> aClass = entity.getClass();
        ArrayList<DBParam> dbParams = new ArrayList<>();

        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
            String name = annotation != null ? annotation.name() : declaredField.getName();
            try {
                Object value = declaredField.get(entity);
                if (value != null) {
                    sql.append(name).append(",");
                    values.append("@").append(name).append(",");
                    dbParams.add(new DBParam(name, value));
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        return sql.append(values);
    }


    public static String getIDName(Class<?> entityClass) {
        String IDName = "id";
        for (Field declaredField : entityClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            EntityID annotation = declaredField.getAnnotation(EntityID.class);
            if (annotation != null) {
                IDName = annotation.name();
            }
        }
        return IDName;
    }
}
