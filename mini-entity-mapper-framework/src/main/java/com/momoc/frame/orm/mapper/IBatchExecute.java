package com.momoc.frame.orm.mapper;


/**
 * 用于批量处理的
 */
public interface IBatchExecute {

    String getSQL();

    DBParam[] getParameters();
}
