package com.momoc.frame.orm.mapper;

import java.util.List;

public interface BaseEntityDeleteMapper<E> {

    boolean deleteById(E id);

    boolean deleteByIds(List<E> ids);

    boolean deleteEntities(DBParam... dbParams);

}
