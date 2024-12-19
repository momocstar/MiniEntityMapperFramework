package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.convert.MapConvertToBean;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 基础
 * T 泛型
 * E 主键类型
 * 所有params参数key可加别名
 */
public interface BaseEntityQueryMapper<T, E> {

    /**
     * 获取表名
     *
     * @return
     */
    default String getTableName(Class<T> entityClass) {
        return SelectSqlFieldGenerate.getTableName(entityClass);
    }

    /**
     * 获取实体的所有字段的查询sql
     *
     * @return
     */
    default String getAllTableQueryField(Class<T> entityClass) {
        return SelectSqlFieldGenerate.getAllTableQueryField(entityClass);
    }

    default String getSimpleTableQuery(Class<T> entityClass) {
        StringBuilder sb = new StringBuilder();
        sb.append("select  * ");
        sb.append(" from ").append(getTableName(entityClass));
        return sb.toString();
    }

    /**
     * 根据主键进行查询
     *
     * @param id
     * @return
     */
    T queryOneById(E id);


    /**
     * 通过主键查询多个实体
     *
     * @param ids
     * @return
     */
    List<T> queryListByIds(Collection<E> ids);




    /**
     * 通过sql查询主键
     *
     * @param params 字段参数
     * @return
     */
    T queryOneByCondition(Map<String, Object> params);

    T queryOneByCondition(DBParams... dbParams);



    /**
     * 通过sql查询主键
     *
     * @param sql    查询sql
     * @param params 参数，，如已带条件则不会自动拼接条件
     * @return
     */
    T queryOneByCondition(String sql, Map<String, Object> params);

    T queryOneByCondition(String sql, DBParams... dbParams);

    /**
     * 查询实体list通过params参数
     *
     * @param params 参数
     * @return
     */
    List<T> queryListByMap(Map<String, Object> params);

    List<T> queryListByMap(DBParams... dbParams);


    /**
     * sql查询实体list通过params参数
     *
     * @param sql    sql
     * @param params 参数
     * @return
     */
    List<T> queryListByMap(String sql, Map<String, Object> params);

    List<T> queryListByMap(String sql, DBParams... dbParams);

    /**
     * ·
     * 统计实体的数量
     *
     * @param params 参数
     * @return
     */
    Long countByMap(Map<String, Object> params);

    Long countByMap(DBParams... dbParams);


    /**
     * 根据sql统计数量
     *
     * @param sql
     * @param params
     * @return
     */
    Long countByMap(String sql, Map<String, Object> params);

    Long countByMap(String sql, DBParams... dbParams);


    /**
     * 根据sql条件获取bean
     *
     * @param sql    sql
     * @param params 参数
     * @param RClass 自己定义的Bean
     * @param <R>    自己定义的Bean类型
     * @return
     */
    <R> R queryBean(String sql, Class<R> RClass, Map<String, Object> params);

    <R> R queryBean(String sql, Class<R> RClass, DBParams... dbParams);

    /**
     * @param params
     * @param RClass
     * @param <R>
     * @return
     */
    <R> R queryBean(Class<R> RClass, Map<String, Object> params);


    <R> R queryBean(Class<R> RClass, DBParams... dbParams);


    /**
     * 根据SQL获取实体类，查询的字段与实体类要遵循规则
     *
     * @param params        参数
     * @param convertToBean 转换方法
     * @param <R>           返回转换后的实体类， 特殊实体类通过构造方法转换时需要实现,入参为Map<String,Object>
     * @return
     */
    <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, Map<String, Object> params);

    <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, DBParams... dbParams);


    /**
     * @param params 参数
     * @param <R>    类型
     * @return
     */
    <R> List<R> queryBeanListByMap(Class<R> RClass, Map<String, Object> params);

    /**
     * @param sql    sql
     * @param params 参数
     * @param <R>    类型
     * @return
     */
    <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, Map<String, Object> params);


    <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, DBParams... dbParams);

    EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, DBParams... dbParams);

    EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, Map<String, Object> params);

    EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, DBParams... dbParams);

    EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, Map<String, Object> params);


    <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, DBParams... dbParams);

    <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params);


    <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params);

    <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, DBParams... dbParams);


}
