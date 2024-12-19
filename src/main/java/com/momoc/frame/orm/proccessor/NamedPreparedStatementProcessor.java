package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.DBParams;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NamedPreparedStatementProcessor implements SqlParamsPreparedProcessor {

    StringBuilder parseSQL;

    PreparedStatement preparedStatement;

    Map<String, Integer> indexMap;


    public NamedPreparedStatementProcessor(Connection connection, StringBuilder SQL, List<DBParams> dbParams) throws SQLException {

        build(connection, SQL, dbParams, Statement.NO_GENERATED_KEYS);
    }

    public NamedPreparedStatementProcessor(Connection connection, StringBuilder SQL, List<DBParams> dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {
        build(connection, SQL, dbParams, Statement.RETURN_GENERATED_KEYS);

    }


    public void build(Connection connection, StringBuilder SQL, List<DBParams> dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {

        indexMap = handlerWhereParams(SQL, dbParams);
        this.parseSQL = SQL;

        if (SQL.indexOf("call") != -1) {
            preparedStatement = connection.prepareCall(SQL.toString());
        } else {
            preparedStatement = connection.prepareStatement(SQL.toString());
        }

    }


    /**
     * 过滤正则存在的问题
     */
    private static final Pattern S_PATTERN_SAFE = Pattern.compile("(?i)(insert|update|delete|alter|drop|truncate)");


    Pattern REPLACE_PARAMS_PATTERN = Pattern.compile("@(\\w+)");

    /**
     * 处理sql  where的参数条件
     *
     * @param sql      解析后的占位sql 带 ?
     * @param dbParams 参数
     * @return index
     */
    public Map<String, Integer> handlerWhereParams(StringBuilder sql, List<DBParams> dbParams) {

        HashMap<String, Integer> indexesMap = new HashMap<>();

        if (sql.indexOf("where") != -1) {
            replacePlaceholder(sql, dbParams, indexesMap);
        } else {
            //没有带where
            sql.append(" where ");
            for (DBParams dbParam : dbParams) {
                sql.append(dbParam.getName()).append(" = ").append('@' + dbParam.getName());
            }
            replacePlaceholder(sql, dbParams, indexesMap);
        }
        return indexesMap;
    }

    private void replacePlaceholder(StringBuilder sql, List<DBParams> dbParams, HashMap<String, Integer> indexesMap) {
        // 使用正则表达式匹配所有的参数占位符
        Matcher matcher = REPLACE_PARAMS_PATTERN.matcher(sql);
        /**
         * 获取参数顺序
         */
        Integer index = 1;
        while (matcher.find()) {
            String key = matcher.group(1);
            indexesMap.put(key, index);
        }
        for (DBParams dbParam : dbParams) {
            String key = '@' + dbParam.getName();
            int start = sql.indexOf(key);
            if (dbParam.getCollectionSize() != null) {
                StringBuilder N = new StringBuilder();
                for (int i = 0; i < dbParam.getCollectionSize(); i++) {
                    N.append("?").append(",");
                }
                N.deleteCharAt(N.length());
                sql.replace(start, start + key.length(), N.toString());
            } else {
                sql.replace(start, start + key.length(), "?");
            }
        }
    }


}
