package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.mapper.DBParam;
import lombok.Getter;

import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class NamedPreparedStatementProcessor  {

    StringBuilder parseSQL;

    @Getter
    PreparedStatement preparedStatement;

    Map<String, Integer> indexesMap = new HashMap<>();



    public NamedPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams) throws SQLException {
        build(connection, SQL, dbParams, Statement.NO_GENERATED_KEYS);
    }

    public NamedPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {
        build(connection, SQL, dbParams, RETURN_GENERATED_KEYS);
    }


    protected void build(Connection connection, StringBuilder SQL, DBParam[] dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {

        handlerWhereParams(SQL, dbParams);
        this.parseSQL = SQL;
        if (SQL.indexOf("call") != -1) {
            preProcessing(SQL, dbParams);
            preparedStatement = connection.prepareCall(SQL.toString());
        } else {
            preProcessing(SQL, dbParams);
            preparedStatement = connection.prepareStatement(SQL.toString(), RETURN_GENERATED_KEYS);
        }
    }







    Pattern REPLACE_PARAMS_PATTERN = Pattern.compile("@(\\w+)");


    //前置处理
    protected void preProcessing(StringBuilder parseSQL, DBParam[] dbParams) {

    }

    /**
     * 处理sql  where的参数条件
     *
     * @param sql      解析后的占位sql 带 ?
     * @param dbParams 参数
     * @return index
     */
    protected  void handlerWhereParams(StringBuilder sql, DBParam[] dbParams) {
        replacePlaceholder(sql, dbParams);
    }

    /**
     *
     * @param sql
     * @param dbParams
     * @return  返回最大的index数
     */
    protected Integer replacePlaceholder(StringBuilder sql, DBParam[] dbParams) {
        // 使用正则表达式匹配所有的参数占位符
        Matcher matcher = REPLACE_PARAMS_PATTERN.matcher(sql);
        /**
         * 获取参数顺序
         */
        Integer index = 1;
        while (matcher.find()) {
            String key = matcher.group(1);
            indexesMap.put(key, index);
            index++;
        }
        int max = index;
        //变偏移量,遇到collection时需要进行偏移
        Integer offset = 0;
        for (DBParam dbParam : dbParams) {
            String key = '@' + dbParam.getName();
            int start = sql.indexOf(key);
            if (dbParam.isCollection()) {
                StringBuilder N = new StringBuilder();
                for (int i = 0; i < dbParam.getCollectionSize(); i++) {
                    N.append("?").append(",");
                }
                offset += dbParam.getCollectionSize() - 1;
                N.deleteCharAt(N.length() - 1);
                sql.replace(start, start + key.length(), N.toString());
            } else {
                int finalIndex = indexesMap.get(dbParam.getName()) + offset;
                indexesMap.put(dbParam.getName(), finalIndex);
                max = Math.max(max, finalIndex);
                sql.replace(start, start + key.length(), "?");
            }
        }
        return max;
    }

    public void setObject(DBParam... dbParams) throws SQLException {
        for (DBParam dbParam : dbParams) {
            Integer index = indexesMap.get(dbParam.getName());
            if (index != null){
                if (dbParam.isCollection()){
                    Collection<?> value = (Collection<?>) dbParam.getValue();
                    int position = index;
                    for (Object o : value) {
                        setValueByType(position, o);
                        position++;
                    }
                } else if (dbParam.getValue() == null){
                    this.preparedStatement.setNull(index, Types.NULL);
                }else{
                    setValueByType(index, dbParam.getValue());
                }
            }else{
                throw new IllegalArgumentException("SQL Parameter not found: " + dbParam.getName());
            }

        }
    }
    public void setValueByType(int position, Object value) throws SQLException {

        if (value == null) {
            this.preparedStatement.setNull(position, Types.NULL);
            return;
        }
        if (value instanceof Integer) {
            this.preparedStatement.setInt(position, (Integer) value);
        }else if (value instanceof Long) {
            this.preparedStatement.setLong(position, (Long) value);
        }else if (value instanceof Double) {
            this.preparedStatement.setDouble(position, (Double) value);
        }else if (value instanceof Float) {
            this.preparedStatement.setFloat(position, (Float) value);
        }else if (value instanceof String) {
            this.preparedStatement.setString(position, (String) value);
        }else if (value instanceof Boolean) {
            this.preparedStatement.setBoolean(position, (Boolean) value);
        } else if (value instanceof BigDecimal) {
            this.preparedStatement.setBigDecimal(position, (BigDecimal)value);
        } else if (value instanceof Byte) {
            this.preparedStatement.setByte(position, (Byte) value);
        } else if (value instanceof Byte[]) {
            this.preparedStatement.setBytes(position, (byte[]) value);
        } else if (value instanceof Date) {

            this.preparedStatement.setDate(position, (Date) value);
        } else if (value instanceof Timestamp) {
            this.preparedStatement.setTimestamp(position, (Timestamp) value);

        } else{
            this.preparedStatement.setObject(position, value);
        }
    }
}
