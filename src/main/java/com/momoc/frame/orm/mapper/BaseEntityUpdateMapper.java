package com.momoc.frame.orm.mapper;

import java.util.List;

public interface BaseEntityUpdateMapper<T, E> {

    boolean updateById(E id, DBParams... dbParams);


    boolean update(List<T> entities);


    boolean update(T entity);

    boolean update(List<T> entities, Boolean updateNUll);

    boolean updateBySQL(String sql, Boolean updateNUll, DBParams... dbParams);

    boolean batchUpdateBySQL(String sql, Boolean updateNUll, DBParams... dbParams);

}
