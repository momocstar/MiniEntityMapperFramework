package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

public interface SqlParamsPreparedProcessor {



    <R> List<Object> handlerWhereParams( StringBuilder sql, Map<String, Object> dbParams, EntityPage<R> entityPage) throws SQLException;
}
