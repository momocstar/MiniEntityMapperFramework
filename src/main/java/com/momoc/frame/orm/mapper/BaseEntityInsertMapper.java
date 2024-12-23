package com.momoc.frame.orm.mapper;

import java.util.Collection;
import java.util.List;

/**
 * 插入接口实现
 */
public interface BaseEntityInsertMapper<T> {


    /**
     *
     * 插入数据
     * @param dbParams 字段参数
     * @return ID
     */
    Long insert(DBParam... dbParams);


    Long insertBySQL(String sql, DBParam... dbParams);

    Long insertBySQL(StringBuilder sql, DBParam... dbParams);

    /**
     * 插入实体
     * @param entity 实体， 插入成功后，设置对应的ID
     * @return ID
     */
    Long insert(T entity);

    /**
     * 批量插入实体类
     * @param entities 插入成功后，设置对应的ID
     * @return 返回插入情况，
     */
    long[] batchEntityInsert(Collection<T> entities);


    /**
     *
     *  空值会更新到数据库
     * @param entities 实体LIst
     * @return
     */
    public long[] insertOnDuplicateUpdate(Collection<T> entities);


    public long[] insertOnDuplicateUpdate( Collection<T> entities, String... fields);
}
