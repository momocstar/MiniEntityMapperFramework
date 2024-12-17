package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.util.EntityMethodUtil;

import java.sql.Array;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedPreparedProcessor implements SqlParamsPreparedProcessor {

    private static NamedPreparedProcessor instance;

    public static NamedPreparedProcessor getInstance(){
        if (instance == null){
            synchronized (NamedPreparedProcessor.class) {
                if (null == instance) {
                    instance = new NamedPreparedProcessor();
                }
                return instance;
            }
        }
        return instance;
    }


    /**
     * 处理sql  where的参数条件
     *
     * @param sql      sql
     * @param dbParams 参数
     * @return
     */
    public  ArrayList<Object> handlerWhereParams( StringBuilder sql, Map<String, Object> dbParams) throws SQLException {

        //自带where
        ArrayList<Object> list = new ArrayList<>();
        if (dbParams == null || dbParams.isEmpty()) {
            return list;
        }
        if (sql.indexOf("where") != -1) {
            // 使用正则表达式匹配所有的参数占位符
            Pattern pattern = Pattern.compile("@(\\w+)");
            Matcher matcher = pattern.matcher(sql);

            //参数顺序
            while (matcher.find()) {
                String key = matcher.group(1);
                Object value = dbParams.get(key) != null ? dbParams.get(key) : null;
                if (value == null) {
                    throw new IllegalArgumentException("Parameter '" + key + "' is not provided in the map");
                }
                if (value instanceof Collection) {
                    Collection collection = (Collection) value;
                    if (!collection.isEmpty()){
                        sql.append(key).append(" in ").append("(");
                        for (Object o : collection) {
                            sql.append("?,");
                            list.add(o);
                        }
                        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
                    }
                }else{
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
                    if (!collection.isEmpty()){
                        sql.append(entry.getKey()).append(" in ").append("(");
                        for (Object o : collection) {
                            sql.append("?,");
                            list.add(o);
                        }
                        sql.deleteCharAt(sql.lastIndexOf(",")).append(")");
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
    public <R> List<Object> handlerWhereParams( StringBuilder sql, Map<String, Object> dbParams, EntityPage<R> entityPage) throws SQLException {
        return handlerWhereParams( sql, dbParams);
    }
}
