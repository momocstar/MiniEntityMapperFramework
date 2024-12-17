package com.momoc.frame.orm;

import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;
import com.momoc.frame.orm.convert.MapConvertToBean;

import java.lang.reflect.Field;
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

    /**
     * 获取实体的所有字段的查询sql
     *
     * @return
     */
    default String getAllTableQueryField(Class<T> entityClass) {
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
        return sb.toString();
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
    List<T> queryListByIds(List<E> ids);


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


    /**
     * @param params        参数
     * @param convertToBean 转换方法
     * @param <R>     返回转换后的实体类， 特殊实体类通过构造方法转换时需要实现,入参为Map<String,Object>
     * @return
     */
    <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean);


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
