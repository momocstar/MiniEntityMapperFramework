package com.momoc.frame.orm;

import com.momoc.frame.orm.convert.MapConvertToBean;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;

import java.lang.reflect.Constructor;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseEntityTemplateMapper<T, E> implements BaseEntityQueryMapper<T, E> {


    private final Logger logger = LoggerFactory.getLogger(BaseEntityTemplateMapper.class);

    public BaseEntityTemplateMapper(Class<T> entityClass, Class<E> eClass) {
        this.entityClass = entityClass;
        allTableQueryField = this.getAllTableQueryField(entityClass);
        this.eClass = eClass;
        this.tableName = this.getTableName(entityClass);
    }


    public Class<T> entityClass;

    public Constructor<T> entityConstructor;


    public Class<E> eClass;

    public DatabaseConnectionPool databaseConnectionPool = new DatabaseConnectionPool();
    public String tableName;

    /**
     * 字段与映射
     */
//    public Map<String, Method> tableFieldNameSetterMap = new HashMap<String, Method>();


    public String allTableQueryField;

    @Override
    public T queryOneById(E id) {
        StringBuilder sql = new StringBuilder(allTableQueryField + " where id = @id limit 1");

        List<T> ts = databaseConnectionPool.queryBeanSql(sql, new HashMap<String, Object>() {{
            put("id", id);
        }}, this.entityClass);
        return ts.isEmpty() ? null : ts.get(0);


    }

//    @Override
//    public T queryOneById(E id) {
//        DataSource dataSource = DatabaseConnectionPool.getDataSource();
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//
//            String sql = getSimpleTableQuery(entityClass) + " where id = ? limit 1";
//
//            PreparedStatement statement = connection.prepareStatement(sql);
//            /**
//             * @Todo 超时可配置
//             */
////                statement.setQueryTimeout(10);
//            statement.setObject(1, id);
//            ResultSet resultSet = statement.executeQuery();
//            List<T> ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, EntityMethodUtil.buildFiledSetterMethodMap(this.entityClass));
//            logger.debug("query sql:{}  params:{}", sql, id);
//            return ts.isEmpty() ? null : ts.get(0);
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//
//        }
//    }


    @Override
    public List<T> queryListByIds(List<E> ids) {
        StringBuilder sql = new StringBuilder(allTableQueryField);
        List<T> ts = databaseConnectionPool.queryBeanSql(sql, new HashMap<String, Object>() {{
            put("id", ids);
        }}, this.entityClass);
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
        List<T> ts = queryListByMap(sql, params);
        return ts.isEmpty() ? null : ts.get(0);
    }

    @Override
    public List<T> queryListByMap(Map<String, Object> params) {
        return null;
    }

    @Override
    public List<T> queryListByMap(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder sqlBD = new StringBuilder(sql);
        return databaseConnectionPool.queryBeanSql(sqlBD, params, this.entityClass);
    }

    @Override
    public Long countByMap(Map<String, Object> params) {


        return countByMap("select count(1) from " + this.tableName, params);
    }

    @Override
    public Long countByMap(String sql, Map<String, Object> params) {

        List<Map<String, Object>> maps = databaseConnectionPool.queryMapSql(new StringBuilder(sql), params);

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
    public <R> R queryBean(String sql, Map<String, Object> params, Class<R> RClass) {
        List<R> ts = queryBeanListByMap(sql, params, RClass);
        return ts.isEmpty() ? null : ts.get(0);
    }

//    @Override
//    public <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean) {
//
//        return ts;
//    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, Class<R> RClass) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder sqlBD = new StringBuilder(sql);
        return databaseConnectionPool.queryBeanSql(sqlBD, params, RClass);
    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        ArrayList<R> rs = new ArrayList<>();
        List<Map<String, Object>> maps = databaseConnectionPool.queryMapSql(new StringBuilder(sql), params);
        for (Map<String, Object> map : maps) {
            R r = convertToBean.convertToBean(map);
            rs.add(r);
        }

        return rs;
    }

    @Override
    public EntityPage<T> queryPageByMap(Map<String, Object> params, EntityPage<T> tEntityPage) {
        return null;
    }

    @Override
    public EntityPage<T> queryPageByMap(String sql, Map<String, Object> params, EntityPage<T> tEntityPage) {
        return null;
    }

    @Override
    public <R> EntityPage<R> queryPageByMap(String sql, Map<String, Object> params, Class<R> RClass, EntityPage<R> entityPage) {
        return null;
    }

    @Override
    public <R> EntityPage<R> queryPageByMap(Map<String, Object> params, Class<R> RClass, EntityPage<R> entityPage) {
        return null;
    }
}
