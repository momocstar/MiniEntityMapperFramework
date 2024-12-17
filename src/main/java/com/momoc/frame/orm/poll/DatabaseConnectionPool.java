package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.proccessor.NamedPageEntityPreparedProcessor;
import com.momoc.frame.orm.proccessor.NamedPreparedProcessor;
import com.momoc.frame.orm.proccessor.SqlParamsPreparedProcessor;
import com.momoc.frame.orm.util.EntityMethodUtil;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class DatabaseConnectionPool {
    /**
     * 注入数据源即可
     */
    @Getter
    private static DataSource dataSource;

    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    static {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC"); // 替换为你的数据库名
        config.setUsername("root"); // 替换为你的数据库用户名
        config.setPassword("123456"); // 替换为你的数据库密码
        config.addDataSourceProperty("cachePrepStmts", "true"); // 启用PSCache
        config.addDataSourceProperty("prepStmtCacheSize", "2500"); // 设置PSCache大小
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "20480"); // 设置PSCache SQL长度限制
        dataSource = new HikariDataSource(config);
    }

    public static <R> List<R> queryBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass) {
        //NamedPreparedProcessor不会使用entityPage
        return queryBeanSql(sql, dbParams, entityClass, null, NamedPreparedProcessor.getInstance());

    }

    public static <R> List<R> queryBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass, EntityPage<R> entityPage,
                                           SqlParamsPreparedProcessor paramsPreparedProcessor) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;
        List<R> ts = new ArrayList<>();

        try {
            connection = dataSource.getConnection();
            List<Object> list = paramsPreparedProcessor.handlerWhereParams(sql, dbParams, entityPage);
            String finalSql = sql.toString();
            logger.debug("Final SQL: {}", finalSql);
            PreparedStatement statement;
            if (finalSql.contains("call")) {
                statement = connection.prepareCall(finalSql);
            } else {
                statement = connection.prepareStatement(finalSql);
            }

            for (int i = 0; i < list.size(); i++) {
                statement.setObject(i + 1, list.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, EntityMethodUtil.buildFiledSetterMethodMap(entityClass));
            return ts;
        } catch (Exception e) {
            logger.error("Error executing query: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    public static <R> List<R> queryPageBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass, EntityPage<R> entityPage) {
        return queryBeanSql(sql, dbParams, entityClass, entityPage, NamedPageEntityPreparedProcessor.getInstance());
    }

    /**
     * 查询后将结果映射成map
     * @param sql
     * @param dbParams
     * @return
     */
    public static List<Map<String, Object>> queryMapSql(StringBuilder sql, Map<String, Object> dbParams) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;

        try {
            connection = dataSource.getConnection();
            ArrayList<Object> list = NamedPreparedProcessor.getInstance().handlerWhereParams( sql, dbParams);
            String finalSql = sql.toString();
            logger.debug("Final SQL: {}", finalSql);
            PreparedStatement statement;
            if (finalSql.contains("call")) {
                statement = connection.prepareCall(finalSql);
            } else {
                statement = connection.prepareStatement(finalSql);
            }

            for (int i = 0; i < list.size(); i++) {
                statement.setObject(i + 1, list.get(i));
            }

            ResultSet resultSet = statement.executeQuery();
            return EntityMethodUtil.queryRsToMap(resultSet);
        } catch (Exception e) {
            logger.error("Error executing query: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error("Error closing connection", e);
                    throw new RuntimeException(e);
                }
            }
        }
    }


//    public <R>  List<R> querySql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass) {
//        DataSource dataSource = DatabaseConnectionPool.getDataSource();
//        Connection connection = null;
//        List<R> ts = new ArrayList<>();
//        try {
//
//            connection = dataSource.getConnection();
//
//            ArrayList<Object> list = new ArrayList<>();
//            // 使用正则表达式匹配所有的参数占位符
//            Pattern pattern = Pattern.compile("@(\\w+)");
//            Matcher matcher = pattern.matcher(sql);
//            while (matcher.find()) {
//                String key = matcher.group(1); // 获取参数名（去除 @）
//                //替换为？
//                sql.replace(matcher.start(), matcher.end(), "?");
//                Object value = dbParams.get(key);
//                list.add(value);
//            }
//            String finalSql = sql.toString();
//            logger.debug("entity mapper query sql: {}", finalSql);
//            PreparedStatement statement = connection.prepareStatement(finalSql);
//
//            for (int i = 0; i < list.size(); i++) {
//                statement.setObject(i + 1, list.get(i));
//            }
//
//            /**
//             * @Todo 超时可配置
//             */
//            ResultSet resultSet = statement.executeQuery();
//            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, EntityMethodUtil.buildFiledSetterMethodMap(entityClass));
//            return ts;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }
//    public <R>  List<R> querySql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass) {
//        DataSource dataSource = DatabaseConnectionPool.getDataSource();
//        Connection connection = null;
//        List<R> ts = new ArrayList<>();
//        try {
//
//            connection = dataSource.getConnection();
//
//            ArrayList<Object> list = new ArrayList<>();
//            // 使用正则表达式匹配所有的参数占位符
//            Pattern pattern = Pattern.compile("@(\\w+)");
//            Matcher matcher = pattern.matcher(sql);
//            while (matcher.find()) {
//                String key = matcher.group(1); // 获取参数名（去除 @）
//                //替换为？
//                sql.replace(matcher.start(), matcher.end(), "?");
//                Object value = dbParams.get(key);
//                list.add(value);
//            }
//            String finalSql = sql.toString();
//            PreparedStatement statement = connection.prepareStatement(finalSql);
//
//            for (int i = 0; i < list.size(); i++) {
//                statement.setObject(i + 1, list.get(i));
//            }
//            logger.debug("entity mapper query sql: {}", finalSql);
//            /**
//             * @Todo 超时可配置
//             */
//            ResultSet resultSet = statement.executeQuery();
//            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, EntityMethodUtil.buildFiledSetterMethodMap(entityClass));
//            return ts;
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        } finally {
//            if (connection != null) {
//                try {
//                    connection.close();
//                } catch (SQLException e) {
//                    throw new RuntimeException(e);
//                }
//            }
//        }
//    }

}