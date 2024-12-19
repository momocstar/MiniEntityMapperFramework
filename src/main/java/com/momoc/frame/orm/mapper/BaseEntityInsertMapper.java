package com.momoc.frame.orm.mapper;

import java.util.Collection;

/**
 * 插入接口实现
 */
public interface BaseEntityInsertMapper<T,E> {


    /**
     *
     * 插入数据
     * @param dbParams 字段参数
     * @return ID
     */
    E insert(DBParams... dbParams);


    /**
     * 插入实体
     * @param entity 实体， 插入成功后，设置对应的ID
     * @return ID
     */
    E insert(T entity);

    /**
     * 批量插入实体类
     * @param entities 插入成功后，设置对应的ID
     * @return 返回插入情况，
     */
    int[] batchEntityInsert(Collection<T> entities);


    public int[] insertOnDuplicateUpdate(Collection<T> entities);
    /**
     * 存在根据唯一索引来更新其他值
     * @param entities 实体
     * @param updateNull 是否更新空值
     * @return
     */
    int[] insertOnDuplicateUpdate(Collection<T> entities, boolean updateNull);

    int[] insertOnDuplicateUpdate(String sql, Collection<T> entities, boolean updateNull);

}
