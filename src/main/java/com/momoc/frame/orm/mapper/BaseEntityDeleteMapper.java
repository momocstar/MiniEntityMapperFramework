package com.momoc.frame.orm.mapper;

public interface BaseEntityDeleteMapper<T,E> {

    boolean delete(T entity);

    boolean deleteById(E id);

    int[] deleteByIds(E ids);


}
