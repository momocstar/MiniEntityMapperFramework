package com.momoc.frame.orm.processor;

import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.mapper.IBatchExecute;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.List;
import java.util.regex.Pattern;

@Getter
public class NamedCreateStatement2Processor {
    static Logger logger = LoggerFactory.getLogger(NamedCreateStatement2Processor.class);

    /**
     *   过滤sql注入内容
     */
    private static final Pattern S_PATTERN_SAFE = Pattern.compile("(?i)(insert|update|delete|alter|drop|truncate|select)");

    Statement statement;

    public NamedCreateStatement2Processor(Connection connection, List<IBatchExecute> iBatchExecutes) throws SQLException {
        build(connection);
        for (IBatchExecute iBatchExecute : iBatchExecutes) {
            addBatch(iBatchExecute.getSQL(), iBatchExecute.getParameters());
        }
    }


    protected void build(Connection connection) throws SQLException {
         this.statement = connection.createStatement();
    }

    public void addBatch(String sql, DBParam[] dbParams) throws SQLException {

        StringBuilder sb = new StringBuilder(sql);
        for (int i = 0; i < dbParams.length; i++) {

            String name = "@" + dbParams[i].getName();
            Object value = dbParams[i].getValue();
            if (value != null){
                String sValue = value.toString();
                if (S_PATTERN_SAFE.matcher(value.toString()).find()) {
                    throw new RuntimeException("parameters are not allowed to contain SQL keywords");
                }

                int index = sb.indexOf(name);
                if (index != -1) {
                    sb.replace(index, index + name.length(), value instanceof String ? "'" + sValue + "'" : sValue);
                }
            }
        }
        if (sb.indexOf("@") != -1){
            throw new SQLException(sb.toString());
        }
        logger.debug(" execute batch sql: {}",  sb.toString());
        this.statement.addBatch(sb.toString());

    }
}
