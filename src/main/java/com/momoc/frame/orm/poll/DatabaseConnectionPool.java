package com.momoc.frame.orm.poll;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


public class DatabaseConnectionPool {
    /**
     * 注入数据源即可
     */
    @Getter
    private static DataSource dataSource;

    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    public static void initializingDataSource(DataSource dataSource) {
        if (dataSource == null) {
            dataSource = getSource();
        }


        DatabaseConnectionPool.dataSource = dataSource;
    }

    static {
        initializingDataSource(null);
    }

    private static DataSource getSource() {
        DataSource dataSource;
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl("jdbc:mysql://localhost:3306/test?serverTimezone=UTC"); // 替换为你的数据库名
        config.setUsername("root"); // 替换为你的数据库用户名
        config.setPassword("123456"); // 替换为你的数据库密码
        config.addDataSourceProperty("cachePrepStmts", "true"); // 启用PSCache
        config.addDataSourceProperty("prepStmtCacheSize", "2500"); // 设置PSCache大小
        config.addDataSourceProperty("prepStmtCacheSqlLimit", "20480"); // 设置PSCache SQL长度限制
        dataSource = new HikariDataSource(config);
        return dataSource;
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