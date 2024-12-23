package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import lombok.Getter;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class EntityInsertSQLFieldGenerate {

    /**
     * 完整实体的插入SQL
     */
    private static final Map<Class<?>, String> CACHE_CLASS_FULL_INSERT_SQL = new ConcurrentHashMap<>();
    /**
     * 完整实体的插入SQL
     */
    private static final Map<Class<?>, String> CACHE_CLASS_FULL_DUPLICATE_INSERT_SQL = new ConcurrentHashMap<>();


    @Getter
    protected String SQL;


    //单实体插入时生成好的参数列表
    @Getter
    protected List<DBParam> dbParamList;

    protected Class<?> entityClass;

    boolean generateNullFiled = false;

    boolean duplicateOnUpdate = false;



    public EntityInsertSQLFieldGenerate(String tableName, Class<?> entityClass) {
        this.entityClass = entityClass;
        //需要生成完整带空行SQL
        this.generateNullFiled = true;
        buildSQLByClass(tableName);

    }


    public EntityInsertSQLFieldGenerate(String tableName, Class<?> entityClass, boolean duplicateOnUpdate) {
        this.entityClass = entityClass;
        this.duplicateOnUpdate = duplicateOnUpdate;
        //需要生成完整带空行SQL
        this.generateNullFiled = true;
        buildSQLByClass(tableName);
    }


    public EntityInsertSQLFieldGenerate(String tableName, Class<?> entityClass, String[] duplicateFields) {
        this.entityClass = entityClass;
        this.generateNullFiled = true;
        buildSQLByClass(tableName, duplicateFields);
    }



    public EntityInsertSQLFieldGenerate(String tableName, Object entity) {
        buildByInstanceEntity(tableName, entity, false);
    }

    /**
     * 生成插入SQL
     *
     * @param tableName         表名
     * @param entity            实体
     * @param generateNullFiled 是否生成空值字段SQL，否只生成当前实体对象有值内容
     * @param <T>
     */
    public <T> EntityInsertSQLFieldGenerate(String tableName, Object entity, boolean generateNullFiled) {
        buildByInstanceEntity(tableName, entity, generateNullFiled);
    }


    /**
     * 通过实例构造插入对象
     *
     * @param tableName         表名
     * @param entity            实体
     * @param generateNullFiled 是否生成空值字段SQL
     */
    private void buildByInstanceEntity(String tableName, Object entity, boolean generateNullFiled) {

        if (generateNullFiled) {
            buildSQLByClass(tableName);
            return;
        }

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
        this.SQL = sql.append(values).toString();
        this.entityClass = entity.getClass();

    }


    /**
     * 仅生成insert SQL，对应的数据list的需要一个生成
     *
     * @param tableName
     */
    private void buildSQLByClass(String tableName) {

        String SQL;
        if (duplicateOnUpdate) {
            SQL = CACHE_CLASS_FULL_DUPLICATE_INSERT_SQL.get(entityClass);
        } else {
            SQL = CACHE_CLASS_FULL_INSERT_SQL.get(entityClass);
        }
        if (SQL != null) {
            this.SQL = SQL;
            return;
        }

        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");
        StringBuilder values = new StringBuilder("values(");
        StringBuilder onUpdate = null;
        if (duplicateOnUpdate) {
            onUpdate = new StringBuilder(" ON DUPLICATE KEY UPDATE ");
        }


        for (Field declaredField : entityClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
            String name = annotation != null ? annotation.name() : declaredField.getName();
            sql.append(name).append(",");
            values.append("@").append(name).append(",");
            if (duplicateOnUpdate) {
                onUpdate.append(name).append("=VALUES(").append(name).append("),");
            }

        }
        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        if (duplicateOnUpdate) {
            onUpdate.deleteCharAt(onUpdate.length() - 1);
            this.SQL = sql.append(values).append(onUpdate).toString();
            CACHE_CLASS_FULL_DUPLICATE_INSERT_SQL.put(entityClass, sql.toString());
        } else {
            this.SQL = sql.append(values).toString();
            CACHE_CLASS_FULL_INSERT_SQL.put(entityClass, sql.toString());
        }
    }

    /**
     * @param tableName
     */
    private void buildSQLByClass(String tableName, String[] fields) {

        StringBuilder sql = new StringBuilder("insert into ").append(tableName).append("(");
        StringBuilder values = new StringBuilder("values(");
        StringBuilder onUpdate = new StringBuilder(" ON DUPLICATE KEY UPDATE ");

        for (Field declaredField : entityClass.getDeclaredFields()) {
            declaredField.setAccessible(true);
            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
            String name = annotation != null ? annotation.name() : declaredField.getName();
            sql.append(name).append(",");
            values.append("@").append(name).append(",");
        }
        for (String field : fields) {
            onUpdate.append(field).append("=VALUES(").append(field).append("),");
        }

        sql.deleteCharAt(sql.length() - 1).append(") ");
        values.deleteCharAt(values.length() - 1).append(")");
        onUpdate.deleteCharAt(onUpdate.length() - 1);
        this.SQL = sql.append(values).append(onUpdate).toString();
    }

    public DBParam[] buildEachParam(Object entity) {

        Field[] declaredFields = this.entityClass.getDeclaredFields();
        DBParam[] dbParams = new DBParam[declaredFields.length];
        for (int i = 0; i < declaredFields.length; i++) {

            try {
                declaredFields[i].setAccessible(true);
                Object value = declaredFields[i].get(entity);
                if (value != null || generateNullFiled) {
                    dbParams[i] = new DBParam(declaredFields[i].getName(), value);
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }

        }
        return dbParams;
    }


    /**
     * 生成INSERT INTO ... ON DUPLICATE KEY UPDATE语句
     * @param tableName         表名
     * @param entity            实体
     * @param generateNullFiled 是否生成空值字段SQL
     * @param <T>
     */
//    public void insertOnDuplicateUpdate(String tableName,   boolean generateNullFiled) {
//        StringBuilder sql = new StringBuilder("INSERT INTO ").append(tableName).append("(");
//        StringBuilder values = new StringBuilder("VALUES(");
//        StringBuilder onUpdate = new StringBuilder("ON DUPLICATE KEY UPDATE ");
//        ArrayList<DBParam> dbParams = new ArrayList<>();
//
//        Class<?> aClass = entity.getClass();
//        for (Field declaredField : aClass.getDeclaredFields()) {
//            declaredField.setAccessible(true);
//            MiniEntityTableFieldName annotation = declaredField.getAnnotation(MiniEntityTableFieldName.class);
//            String name = annotation != null ? annotation.name() : declaredField.getName();
//
//            try {
//                Object value = declaredField.get(entity);
//                if (value != null || generateNullFiled) {
//                    sql.append(name).append(",");
//                    values.append("@").append(name).append(",");
//                    if (generateNullFiled || value != null) {
//                        onUpdate.append(name).append("=VALUES(").append(name).append("),");
//                    }
//                    dbParams.add(new DBParam(name, value));
//                }
//            } catch (IllegalAccessException e) {
//                throw new RuntimeException(e);
//            }
//        }
//
//        sql.deleteCharAt(sql.length() - 1).append(") ");
//        values.deleteCharAt(values.length() - 1).append(")");
//        onUpdate.deleteCharAt(onUpdate.length() - 1);
//
//        this.SQL = sql.append(values).append(onUpdate);
//        this.dbParamList = dbParams;
//        this.entityClass = entity.getClass();
//    }
}


