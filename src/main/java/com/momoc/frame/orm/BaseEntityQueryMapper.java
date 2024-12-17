package com.momoc.frame.orm;

import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;
import com.momoc.frame.orm.convert.MapConvertToBean;
import com.momoc.frame.orm.util.EntityMethodUtil;

import java.lang.reflect.Field;
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

    /**
     * 通过sql查询主键
     *
     * @param sql    查询sql
     * @param params 参数，，如已带条件则不会自动拼接条件
     * @return
     */
    T queryOneByCondition(String sql, Map<String, Object> params);


    /**
     * 查询实体list通过params参数
     *
     * @param params 参数
     * @return
     */
    List<T> queryListByMap(Map<String, Object> params);

    /**
     * sql查询实体list通过params参数
     *
     * @param sql    sql
     * @param params 参数
     * @return
     */
    List<T> queryListByMap(String sql, Map<String, Object> params);


    /**
     * ·
     * 统计实体的数量
     *
     * @param params 参数
     * @return
     */
    Long countByMap(Map<String, Object> params);


    /**
     * 根据sql统计数量
     *
     * @param sql
     * @param params
     * @return
     */
    Long countByMap(String sql, Map<String, Object> params);


    /**
     * 根据sql条件获取bean
     *
     * @param sql          sql
     * @param params       参数
     * @param RClass 自己定义的Bean
     * @param <R>    自己定义的Bean类型
     * @return
     */
    <R> R queryBean(String sql, Map<String, Object> params, Class<R> RClass);

    <R> R queryBean( Map<String, Object> params, Class<R> RClass);

    /**
     * 根据SQL获取实体类，查询的字段与实体类要遵循规则
     * @param params        参数
     * @param convertToBean 转换方法
     * @param <R>     返回转换后的实体类， 特殊实体类通过构造方法转换时需要实现,入参为Map<String,Object>
     * @return
     */
    <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean);



    /**
     * @param params    参数
     * @param <R> 类型
     * @return
     */
    <R> List<R> queryBeanListByMap(Map<String, Object> params, Class<R> RClass);

    /**
     * @param sql       sql
     * @param params    参数
     * @param <R> 类型
     * @return
     */
    <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, Class<R> RClass);


    EntityPage<T> queryPageByMap(Map<String, Object> params, EntityPage<T> tEntityPage);

    EntityPage<T> queryPageByMap(String sql, Map<String, Object> params, EntityPage<T> tEntityPage);


    <R> EntityPage<R> queryPageByMap(String sql, Map<String, Object> params, Class<R> RClass, EntityPage<R>  entityPage);


    <R> EntityPage<R> queryPageByMap(Map<String, Object> params, Class<R> RClass,  EntityPage<R>  entityPage);


}
