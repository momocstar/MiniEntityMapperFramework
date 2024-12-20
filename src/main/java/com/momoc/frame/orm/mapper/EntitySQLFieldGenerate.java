package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntitySQLFieldGenerate {

    protected StringBuilder SQL;


    protected List<DBParam> dbParamList;

    public <T> EntitySQLFieldGenerate(String tableName, T entity) {
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

        this.dbParamList = dbParams;
        this.SQL = sql.append(values);
    }

    public StringBuilder getSQL() {
        return SQL;
    }

    public List<DBParam> getDbParamList() {
        return dbParamList;
    }
}
