package com.momoc.frame.orm.mapper;

import java.util.List;

public interface BaseEntityUpdateMapper<T, E> {

    boolean updateById(E id, DBParam... dbParams);


    boolean update(List<T> entities);


    boolean update(T entity);

    boolean update(List<T> entities, Boolean updateNUll);

    boolean updateBySQL(String sql, Boolean updateNUll, DBParam... dbParams);

    boolean batchUpdateBySQL(String sql, Boolean updateNUll, DBParam... dbParams);

}
