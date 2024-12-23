package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.proccessor.NamedPreparedStatementProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 查询sql
 */
public class SessionUpdateOrInsertExecute {


    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);



    /**
     *
     * @param templateSQL 批处理的模板sql
     * @param dbParams
     * @return
     */
    public static long[] batchExecute(StringBuilder templateSQL, List<DBParam[]> dbParams) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            NamedPreparedStatementProcessor namedPreparedStatementProcessor;

            boolean insert = templateSQL.indexOf("insert") != -1;
//            if (insert){
            namedPreparedStatementProcessor = new NamedPreparedStatementProcessor(connection, templateSQL,
                    dbParams.get(0), Statement.RETURN_GENERATED_KEYS);
//            }else{
//                namedPreparedStatementProcessor = new NamedPreparedStatementProcessor(connection, templateSQL,
//                        dbParams.getFirst());
//            }
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();
            for (DBParam[] list : dbParams) {
                preparedStatement.clearParameters();
                namedPreparedStatementProcessor.setObject(list);
                preparedStatement.addBatch();
            }
            int[] ints = preparedStatement.executeBatch();
            long[] rs = new long[ints.length];

            if (insert) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys != null) {
                    int pos = 0;
                    while (generatedKeys.next()) {
                        long generatedKey = generatedKeys.getLong(1);
                        rs[pos] = generatedKey;
                    }
                    return rs;
                }
            }
            for (int i = 0; i < ints.length; i++) {
                rs[i] = ints[i];
            }
            return rs;
        } catch (Exception e) {
            logger.error("Error executing query: {}", templateSQL, e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
        return new long[0];

    }

    /**
     * 查询后将结果映射成map
     *
     * @param templateSQL 批处理的模板sql
     * @param dbParams
     * @return ID
     */
    public static Long execute(StringBuilder templateSQL, DBParam[] dbParams) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            NamedPreparedStatementProcessor namedPreparedStatementProcessor = new NamedPreparedStatementProcessor(connection, templateSQL,
                    dbParams, Statement.RETURN_GENERATED_KEYS);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();

            namedPreparedStatementProcessor.setObject(dbParams);
            logger.debug(" execute sql:{}", preparedStatement.toString());
            preparedStatement.execute();

            boolean insert = templateSQL.indexOf("insert") != -1;
            if (insert) {
                ResultSet generatedKeys = preparedStatement.getGeneratedKeys();
                if (generatedKeys != null) {
                    if (generatedKeys.next()) {
                        return generatedKeys.getLong(1);
                    }
                }
            }
            return 0L;
        } catch (Exception e) {
            logger.error("Error executing query: {}", templateSQL, e);
            return -1L;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                }
            }
        }
    }

}
