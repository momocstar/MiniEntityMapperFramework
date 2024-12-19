package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.DBParams;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface SqlParamsPreparedProcessor {



    default <R> List<Object> handlerWhereParams(StringBuilder sql, Map<String, Object> dbParams, EntityPage<R> entityPage) throws SQLException{
        return null;
    };


    /**
     *
     * @param sql sql
     * @param dbParams 参数
     * @return 返回每个批处理的List
     * @param <R>
     * @throws SQLException
     */
    default <R>  List<List<Object>> handlerBatchUpdateOrInsertParams(StringBuilder sql, List<List<DBParams>> dbParams) throws SQLException {
        return new ArrayList<>();
    };

}
