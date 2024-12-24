package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.mapper.DBParam;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 用于处理查询和自动填充where条件的处理器
 */
public class NamedQueryPreparedStatementProcessor extends NamedPreparedStatementProcessor {

    public NamedQueryPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams) throws SQLException {
        super(connection, SQL, dbParams);
    }

    public NamedQueryPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {
        super(connection, SQL, dbParams, RETURN_GENERATED_KEYS);
    }


    /**
     * 处理sql  where的参数条件
     *
     * @param sql      解析后的占位sql 带 ?
     * @param dbParams 参数
     * @return index
     */
    protected void handlerWhereParams(StringBuilder sql, DBParam[] dbParams) {
        if (sql.indexOf("where") == -1) {
            //没有带where
            sql.append(" where ");
            for (DBParam dbParam : dbParams) {
                if (dbParam.isCollection()) {
                    sql.append(dbParam.getName()).append(" in ( ").append('@' + dbParam.getName()).append(" )").append(" and ");
                } else {
                    sql.append(dbParam.getName()).append(" = ").append('@' + dbParam.getName()).append(" and ");
                }
            }
            //删除多余的AND
            sql.delete(sql.length() - 4, sql.length());
        }
        super.handlerWhereParams(sql, dbParams);
    }
}
