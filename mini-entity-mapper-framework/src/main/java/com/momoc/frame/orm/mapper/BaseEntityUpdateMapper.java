package com.momoc.frame.orm.mapper;

import java.util.List;

public interface BaseEntityUpdateMapper<T, E> {

    boolean updateById(E id, DBParam... dbParams);


    long[] update(List<T> entities);


    boolean update(T entity);
    boolean update(T entity, Boolean updateNUll);

    long[] update(List<T> entities, Boolean updateNUll);

    boolean updateBySQL(String sql,  DBParam... dbParams);


}
