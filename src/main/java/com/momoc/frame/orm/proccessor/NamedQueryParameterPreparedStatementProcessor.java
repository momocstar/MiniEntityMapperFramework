package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.DBParams;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class NamedQueryParameterPreparedStatementProcessor implements SqlParamsPreparedProcessor {

    String parseSQL;

    PreparedStatement preparedStatement;

    Map<String,Integer> indexMap;

    /**
     * 过滤正则存在的问题
     */
    private static final Pattern S_PATTERN_SAFE = Pattern.compile("(?i)(insert|update|delete|alter|drop|truncate)");


    private static NamedQueryParameterPreparedStatementProcessor instance;

    public static NamedQueryParameterPreparedStatementProcessor getInstance() {
        if (instance == null) {
            synchronized (NamedQueryParameterPreparedStatementProcessor.class) {
                if (null == instance) {
                    instance = new NamedQueryParameterPreparedStatementProcessor();
                }
                return instance;
            }
        }
        return instance;
    }

    Pattern REPLACE_PARAMS_PATTERN = Pattern.compile("@(\\w+)");

    /**
     * 处理sql  where的参数条件
     *
     * @param sql      sql
     * @param dbParams 参数
     * @return
     */
    public ArrayList<Object> handlerWhereParams(StringBuilder sql, Map<String, Object> dbParams) throws SQLException {

        //自带where
        ArrayList<Object> list = new ArrayList<>();
        if (dbParams == null || dbParams.isEmpty()) {
            return list;
        }
        if (sql.indexOf("where") != -1) {
            // 使用正则表达式匹配所有的参数占位符

            Matcher matcher = REPLACE_PARAMS_PATTERN.matcher(sql);

            //参数顺序
            while (matcher.find()) {
                String key = matcher.group(1);
                Object value = dbParams.get(key) != null ? dbParams.get(key) : null;
                if (value == null) {
                    throw new IllegalArgumentException("Parameter '" + key + "' is not provided in the map");
                }
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()) {
                        sql.append(key).append(" in ").append("(");
                        for (Object o : collection) {
                            sql.append("?,");
                            list.add(o);
                        }
                        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
                    }
                } else {
                    // 添加参数值到列表
                    list.add(value);
                }
            }

            for (Map.Entry<String, Object> entry : dbParams.entrySet()) {
                String key = "@" + entry.getKey();
                int index = sql.indexOf(key);
                if (index != -1) {
                    sql.replace(index, index + key.length(), "?");
                } else {
                    throw new IllegalArgumentException("Parameter '" + key + "' is not provided in the map");
                }
            }
        } else {
            //没有带where
            sql.append(" where ");
            for (Map.Entry<String, Object> entry : dbParams.entrySet()) {

                Object value = entry.getValue();
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()) {
                        sql.append(entry.getKey()).append(" in ").append("(");
                        for (Object o : collection) {
                            sql.append("?,");
                            list.add(o);
                        }
                        sql.deleteCharAt(sql.length() - 1).append(")");
                    }
                } else {
                    sql.append(entry.getKey()).append(" = ").append("?");
                    list.add(value);
                }
            }

        }
        return list;
    }


    @Override
    public <R> List<Object> handlerWhereParams(StringBuilder sql, Map<String, Object> dbParams, EntityPage<R> entityPage) throws SQLException {
        return handlerWhereParams(sql, dbParams);
    }


    @Override
    public <R> List<List<Object>> handlerBatchUpdateOrInsertParams(StringBuilder sql, List<List<DBParams>> dbEntityParamsList) throws SQLException {
        Matcher matcher = REPLACE_PARAMS_PATTERN.matcher(sql);
        ArrayList<List<Object>> rs = new ArrayList<>();
        //参数顺序
        List<String> keySort = new ArrayList<>();

        while (matcher.find()) {
            String key = matcher.group(1);
            keySort.add(key);
        }
        //替换为？号
        for (String key : keySort) {
            String indexKey = "@" + key;
            int index = sql.indexOf(indexKey);
            if (index != -1) {
                sql.replace(index, index + key.length(), "?");
            }
        }
        //预处理参数
        for (List<DBParams> dbParams : dbEntityParamsList) {
            Map<String, DBParams> dbParam = dbParams.stream().collect(Collectors.toMap(DBParams::getName, k -> k));
            ArrayList<Object> list = new ArrayList<>();
            for (String key : keySort) {
                if (dbParam.containsKey(key)) {
                    list.add(dbParam.get(key));
                }
            }
            if (!list.isEmpty()) {
                rs.add(list);
            }
        }
//        for (Map<String, Object> dbParam : dbParams) {
//            ArrayList<Object> list = new ArrayList<>();
//            for (String key : keySort) {
//                if (dbParam.containsKey(key)) {
//                    list.add(dbParam.get(key));
//                }
//            }
//            if (!list.isEmpty()) {
//                rs.add(list);
//            }
//        }
        return rs;
    }
}
