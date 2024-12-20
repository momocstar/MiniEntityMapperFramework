//package com.momoc.frame.orm.poll;
//
//import com.momoc.frame.orm.proccessor.NamedPreparedProcessor;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import javax.sql.DataSource;
//import java.sql.*;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Map;
//
//
///**
// * 查询sql
// */
//public class SessionUpdateOrInsertExecute {
//
//
//    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);
//
//    /**
//     * 查询后将结果映射成map
//     *
//     * @param templateSQL 批处理的模板sql
//     * @param dbParams
//     * @return
//     */
//    public static long[] batchExecute(StringBuilder templateSQL, List<Map<String, Object>> dbParams) {
//        DataSource dataSource = DatabaseConnectionPool.getDataSource();
//        Connection connection = null;
//
//        try {
//            connection = dataSource.getConnection();
//            List<List<Object>> lists = NamedPreparedProcessor.getInstance().handlerBatchUpdateOrInsertParams(templateSQL, dbParams);
//            String finalSql = templateSQL.toString();
//            logger.debug("Final SQL: {}", finalSql);
//            PreparedStatement statement = connection.prepareStatement(finalSql, Statement.RETURN_GENERATED_KEYS);
//            for (List<Object> list : lists) {
//                for (int i = 0; i < list.size(); i++) {
//                    statement.clearParameters();
//                    statement.setObject(i + 1, list.get(i));
//                    statement.addBatch();
//                }
//            }
//            int[] ints = statement.executeBatch();
//
//            long[] rs = new long[ints.length];
//            ResultSet generatedKeys = statement.getGeneratedKeys();
//            if (generatedKeys != null) {
//                int pos = 0;
//
//                while (generatedKeys.next()) {
//                    long generatedKey = generatedKeys.getLong(1);
//                    rs[pos] = generatedKey;
//                }
//                return rs;
//            }
//            for (int i = 0; i < ints.length; i++) {
//                rs[i] = ints[i];
//            }
//
//            return rs;
//        } catch (Exception e) {
//            if (connection != null) {
//                try {
//                    connection.rollback();
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//            logger.error("Error executing query: {}", templateSQL, e);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    logger.error("Error closing connection", e);
//                }
//            }
//        }
//        return new long[0];
//
//    }
//
//    /**
//     * 查询后将结果映射成map
//     *
//     * @param templateSQL 批处理的模板sql
//     * @param dbParams
//     * @return ID
//     */
//    public static Long execute(StringBuilder templateSQL, List<Map<String, Object>> dbParams) {
//        DataSource dataSource = DatabaseConnectionPool.getDataSource();
//        Connection connection = null;
//        try {
//            connection = dataSource.getConnection();
//
//            List<List<Object>> lists = NamedPreparedProcessor.getInstance().handlerBatchUpdateOrInsertParams(templateSQL, dbParams);
//            String finalSql = templateSQL.toString();
//            logger.debug("Final SQL: {}", finalSql);
//            PreparedStatement statement = connection.prepareStatement(finalSql);
//            for (List<Object> list : lists) {
//                for (int i = 0; i < list.size(); i++) {
//                    statement.clearParameters();
//                    statement.setObject(i + 1, list.get(i));
//                    statement.addBatch();
//                }
//            }
//
//            statement.executeBatch();
//        } catch (Exception e) {
//            if (connection != null) {
//                try {
//                    connection.rollback();
//                } catch (SQLException ex) {
//                    throw new RuntimeException(ex);
//                }
//            }
//            logger.error("Error executing query: {}", templateSQL, e);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    logger.error("Error closing connection", e);
//                }
//            }
//        }
//        return null;
//
//    }
//
//}
