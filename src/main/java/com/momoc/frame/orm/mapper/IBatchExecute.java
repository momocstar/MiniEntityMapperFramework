package com.momoc.frame.orm.mapper;

import java.util.List;

/**
 * 用于批量处理的
 */
public interface IBatchExecute {

    String getSQL();

    DBParam[] getParameters();
}
