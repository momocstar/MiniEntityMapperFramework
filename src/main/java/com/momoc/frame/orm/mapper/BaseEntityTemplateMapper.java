package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.convert.MapConvertToBean;

import java.util.*;

import com.momoc.frame.orm.poll.SessionQueryExecute;
import com.momoc.frame.orm.poll.SessionUpdateOrInsertExecute;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseEntityTemplateMapper<T, E> implements BaseEntityQueryMapper<T, E>, BaseEntityInsertMapper<T> {


    private final Logger logger = LoggerFactory.getLogger(BaseEntityTemplateMapper.class);

    public BaseEntityTemplateMapper(Class<T> entityClass, Class<E> eClass) {
        this.entityClass = entityClass;
        allTableQueryField = this.getAllTableQueryField(entityClass);
        this.eClass = eClass;
        this.tableName = this.getTableName(entityClass);
    }


    /**
     * 实体类Class
     */
    public Class<T> entityClass;


    /**
     * 主键类Class
     */
    public Class<E> eClass;


    /**
     * 表名
     */
    public String tableName;

    /**
     * 字段与映射
     */
//    public Map<String, Method> tableFieldNameSetterMap = new HashMap<String, Method>();


    @Getter
    public String allTableQueryField;

    @Override
    public T queryOneById(E id) {
        StringBuilder sql = new StringBuilder(allTableQueryField + " where id = @id limit 1");

        List<T> ts = SessionQueryExecute.queryBeanSql(sql, this.entityClass, (new DBParam("id", id)));
        return ts.isEmpty() ? null : ts.get(0);


    }


    @Override
    public List<T> queryListByIds(Collection<E> ids) {
        StringBuilder sql = new StringBuilder(allTableQueryField);
        List<T> ts = SessionQueryExecute.queryBeanSql(sql, this.entityClass, new DBParam("id", ids));
        return ts;
    }

    /**
     * @param params 字段参数,支持基本数据类型和List
     * @return
     */
    @Override
    public T queryOneByCondition(Map<String, Object> params) {
        return queryOneByCondition(allTableQueryField, params);
    }

    @Override
    public T queryOneByCondition(String sql, Map<String, Object> params) {
        return queryOneByCondition(sql, this.buildQueryDBParams(params));

    }

    @Override
    public List<T> queryListByMap(Map<String, Object> params) {
        return queryListByMap(allTableQueryField, params);
    }

    @Override
    public List<T> queryListByMap(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        return queryListByMap(allTableQueryField, this.buildQueryDBParams(params));

    }

    @Override
    public Long countByMap(Map<String, Object> params) {
        return countByMap("select count(1) from " + this.tableName, params);
    }


    @Override
    public Long countByMap(String sql, Map<String, Object> params) {
        return countByMap(sql, this.buildQueryDBParams(params));
    }

    @Override
    public <R> R queryBean(Class<R> RClass, Map<String, Object> params) {
        String queryFieldSql = SQLFieldGenerate.getAllTableQueryField(RClass);

        return queryBean(queryFieldSql, RClass, this.buildQueryDBParams(params));

    }


    @Override
    public <R> R queryBean(String sql, Class<R> RClass, Map<String, Object> params) {
        List<R> ts = queryBeanListByMap(sql, RClass, this.buildQueryDBParams(params));
        return ts.isEmpty() ? null : ts.get(0);
    }

    //    @Override
//    public <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean) {
//
//        return ts;
//    }
    @Override
    public <R> List<R> queryBeanListByMap(Class<R> RClass, Map<String, Object> params) {
        String tableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryBeanListByMap(tableQueryField, RClass, params);
    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, RClass, this.buildQueryDBParams(params));
    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        return queryBeanListByMap(sql, convertToBean, this.buildQueryDBParams(params));
    }

    private DBParam[] buildQueryDBParams(Map<String, Object> params) {
        DBParam[] dbParams = new DBParam[params.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dbParams[i++] = new DBParam(entry.getKey(), entry.getValue());
        }
        return dbParams;
    }


    @Override
    public EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, Map<String, Object> params) {
        return queryPageByMap(allTableQueryField, tEntityPage, params);
    }

    @Override
    public EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, Map<String, Object> params) {
        return queryPageByMap(sql, this.entityClass, tEntityPage, params);
    }


    @Override
    public <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, DBParam... dbParams) {
        String from = sql.substring(sql.indexOf("from"));
        Long total = countByMap("select count(*) " + from, dbParams);
        entityPage.setTotal(total);
        StringBuilder sqlBD = new StringBuilder(sql);

        List<R> ts = SessionQueryExecute.queryPageBeanSql(sqlBD, RClass, entityPage, dbParams);
        entityPage.setPageData(ts);
        return entityPage;
    }


    @Override
    public <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params) {
        return queryPageByMap(sql, RClass, entityPage, this.buildQueryDBParams(params));
    }

    @Override
    public <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params) {
        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryPageByMap(allTableQueryField, RClass, entityPage, params);
    }

    public <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, DBParam... dbParams) {

        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryPageByMap(allTableQueryField, RClass, entityPage, dbParams);
    }

//    public Map<String, Object> buildQueryMap(DBParam... dbParams) {
//        HashMap<String, Object> queryMap = new HashMap<>();
//        for (DBParam dbParam : dbParams) {
//            queryMap.put(dbParam.getName(), dbParam.getValue());
//        }
//        return queryMap;
//    }

    @Override
    public EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, DBParam... dbParams) {
        return queryPageByMap(this.entityClass, tEntityPage, dbParams);
    }

    @Override
    public EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, DBParam... dbParams) {
        return queryPageByMap(sql, this.entityClass, tEntityPage, dbParams);

    }

    @Override
    public T queryOneByCondition(DBParam... dbParams) {
        return queryOneByCondition(allTableQueryField, dbParams);
    }

    @Override
    public T queryOneByCondition(String sql, DBParam... dbParams) {
        List<T> ts = queryListByMap(sql, dbParams);
        return ts.isEmpty() ? null : ts.get(0);
//        return queryOneByCondition(sql, this.buildQueryMap(dbParams));
    }

    @Override
    public List<T> queryListByMap(DBParam... dbParams) {
        return queryListByMap(allTableQueryField, dbParams);
    }

    @Override
    public List<T> queryListByMap(String sql, DBParam... dbParams) {
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, this.entityClass, dbParams);

    }

    @Override
    public Long countByMap(DBParam... dbParams) {
        return countByMap("select count(*) from " + tableName, dbParams);
    }

    @Override
    public Long countByMap(String sql, DBParam... dbParams) {
        List<Map<String, Object>> maps = SessionQueryExecute.queryMapSql(new StringBuilder(sql), dbParams);

        if (!maps.isEmpty()) {
            Map<String, Object> dataRow = maps.get(0);
            if (!dataRow.isEmpty()) {
                for (Object value : dataRow.values()) {
                    return (Long) value;
                }
            }
        }
        return null;
    }

    @Override
    public <R> R queryBean(String sql, Class<R> RClass, DBParam... dbParams) {
        List<R> ts = queryBeanListByMap(sql, RClass, dbParams);
        return ts == null || ts.isEmpty() ? null : ts.get(0);
    }


    @Override
    public <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, DBParam... dbParams) {


        ArrayList<R> rs = new ArrayList<>();
        List<Map<String, Object>> maps = SessionQueryExecute.queryMapSql(new StringBuilder(sql), dbParams);
        for (Map<String, Object> map : maps) {
            R r = convertToBean.convertToBean(map);
            rs.add(r);
        }
        return rs;

    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, DBParam... dbParams) {
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, RClass, dbParams);

    }

    @Override
    public <R> List<R> queryBeanListByMap(Class<R> RClass, DBParam... dbParams) {

        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryBeanListByMap(allTableQueryField, RClass, dbParams);
//        return List.of();
    }

    @Override
    public Long insert(DBParam... dbParams) {
        StringBuilder sql = SQLFieldGenerate.generaInsertSQL(tableName, dbParams);
        return insertBySQL(sql, dbParams);
    }
    @Override

    public Long insertBySQL(StringBuilder sql, DBParam... dbParams){
        return SessionUpdateOrInsertExecute.execute(sql, dbParams);
    }


    @Override
    public Long insertBySQL(String sql, DBParam... dbParams) {
        return SessionUpdateOrInsertExecute.execute(new StringBuilder(sql), dbParams);
    }

    @Override
    public Long insert(T entity) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, entity);
        DBParam[] array = entityInsertSQLFieldGenerate.getDbParamList().toArray(new DBParam[0]);
        return insertBySQL(entityInsertSQLFieldGenerate.getSQL(), array);
    }

    @Override
    public long[] batchEntityInsert(Collection<T> entities) {

        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass);

        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }

        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }

    @Override
    public long[] insertOnDuplicateUpdate(Collection<T> entities) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass, true);

        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }

        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }


    @Override
    public long[] insertOnDuplicateUpdate( Collection<T> entities, String... fields) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass, fields);
        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }
        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }
}
