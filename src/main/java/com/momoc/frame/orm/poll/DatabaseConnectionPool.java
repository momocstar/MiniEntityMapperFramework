package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.transaction.EntityTransactionManager;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


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

    public static Connection getConnection() throws SQLException {
        if (EntityTransactionManager.isOpenTransaction()) {
            return EntityTransactionManager.getConnection();
        }else{
            //没有开启事务则自身获取数据库连接
            return dataSource.getConnection();
        }
    }

    public static void close(Connection connection) {
        //没有手动在方法外开起事务，直接被关闭
        if (connection != null && !EntityTransactionManager.isOpenTransaction()) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Error closing connection", e);
            }
        }
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
        config.setAutoCommit(false);
        dataSource = new HikariDataSource(config);
        return dataSource;
    }

}