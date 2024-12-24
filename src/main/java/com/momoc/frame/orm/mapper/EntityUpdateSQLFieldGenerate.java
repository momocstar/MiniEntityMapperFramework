package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.EntityID;
import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.util.ArrayUtil;

import java.lang.reflect.Field;
import java.util.ArrayList;

public class EntityUpdateSQLFieldGenerate implements IBatchExecute {


    protected String sql;

    protected DBParam[] dbParams;


    EntityUpdateSQLFieldGenerate(String tableName, DBParam[] whereParams, DBParam[] setParams) {
        buildByDBParam(tableName, whereParams, setParams);
    }

    EntityUpdateSQLFieldGenerate(String sql, DBParam[] dbParams) {
        this.sql = sql;
        this.dbParams = dbParams;
    }

    EntityUpdateSQLFieldGenerate(String tableName, Object entity) {
        buildByInstanceEntity(tableName, entity, false);
    }

    EntityUpdateSQLFieldGenerate(String tableName, Object entity, boolean generateNullFiled) {
        buildByInstanceEntity(tableName, entity, generateNullFiled);
    }


    /**
     * 通过实例构造更新对象
     *
     * @param tableName         表名
     * @param entity            实体
     * @param generateNullFiled 是否生成空值字段SQL
     */
    private void buildByInstanceEntity(String tableName, Object entity, boolean generateNullFiled) {


        StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");

        Class<?> aClass = entity.getClass();
        ArrayList<DBParam> dbParams = new ArrayList<>();

        DBParam idParam = null;

        for (Field declaredField : aClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
            String name = annotation != null ? annotation.name() : declaredField.getName();

            EntityID entityID = declaredField.getAnnotation(EntityID.class);

            try {
                Object value = declaredField.get(entity);

                if (entityID != null || "id".equalsIgnoreCase(name)) {
                    idParam = new DBParam(declaredField.getName(), value);
                    continue;
                }
                if (value != null || generateNullFiled) {
                    sql.append(name).append(" = ");
                    sql.append("@").append(name).append(",");
                    dbParams.add(new DBParam(name, value));
                }

            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        }
        if (idParam == null) {
            throw new RuntimeException("Entity is missing primary key，use EntityID annotation to specify the primary key");
        }
        sql.deleteCharAt(sql.length() - 1).append(" where ").append(idParam.getName()).append(" = ").append("@").append(idParam.getName());
        dbParams.add(idParam);
        this.dbParams = dbParams.toArray(new DBParam[0]);
        this.sql = sql.toString();
    }

    private void buildByDBParam(String tableName, DBParam[] whereParams, DBParam[] setParams) {

        StringBuilder sql = new StringBuilder("update ").append(tableName).append(" set ");

        ArrayList<DBParam> dbParams = new ArrayList<>();

        for (DBParam setParam : setParams) {
            String name = setParam.getName();
            sql.append(name).append(" = ");
            sql.append("@").append(name).append(",");
            dbParams.add(new DBParam(name, name));
        }

        sql.deleteCharAt(sql.length() - 1);

        sql.append(" where ");
        for (DBParam whereParam : whereParams) {
            sql.append(whereParam.getName()).append(" = ").append("@").append(whereParam.getName());
        }
        this.dbParams = ArrayUtil.addElements(setParams, whereParams);
        this.sql = sql.toString();
    }

    @Override
    public String getSQL() {
        return sql;
    }

    @Override
    public DBParam[] getParameters() {
        return dbParams;
    }
}


