package com.momoc.frame.orm.transaction;

import com.momoc.frame.orm.poll.DatabaseConnectionPool;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;


/**
 * 编程式事务管理器
 */
public class EntityTransactionManager {

    private static final ThreadLocal<Connection> connectionThreadLocal = new ThreadLocal<>();

    private static final ThreadLocal<Boolean> isTransactionActive = new ThreadLocal<>();

    private static final ThreadLocal<Integer> transactionCounter = new ThreadLocal<>();


    /**
     * 当前线程是否开起来事务
     * @return
     */
    public static boolean isOpenTransaction(){
        return isTransactionActive.get() != null && isTransactionActive.get();

    }

    public static void startTransaction() throws SQLException {
        if (isTransactionActive.get() == null || !isTransactionActive.get()) {
            //开始一个新的事务
            isTransactionActive.set(true);
            transactionCounter.set(1);
            //start a new transaction
            DataSource dataSource = DatabaseConnectionPool.getDataSource();
            Connection connection = dataSource.getConnection();
            connection.unwrap(java.sql.Connection.class).setAutoCommit(false);
            connection.setAutoCommit(false);
            connectionThreadLocal.set(dataSource.getConnection());
        } else {
            //已经有一个事务在进行，计数器加1
            transactionCounter.set(transactionCounter.get() + 1);
        }
    }

    public static void commitTransaction() throws SQLException {
        if (transactionCounter.get() > 1) {
            //还有其它事务在进行，计数器减1，但是并不提交事务
            transactionCounter.set(transactionCounter.get() - 1);
        } else {
            //所有的事务都完成，进行提交
            isTransactionActive.set(false);
            transactionCounter.set(0);
            //commit the transaction
            try {
                connectionThreadLocal.get().commit();
            }finally {
                //提交失败也要释放连接
                releaseConnect();
            }

        }
    }

    public static void rollbackTransaction() {
        //无论什么情况，所有的事务都需要回滚
        isTransactionActive.set(false);
        transactionCounter.set(0);
        //rollback the transaction
        try {
            connectionThreadLocal.get().rollback();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }finally {
            //回滚后释放连接
            releaseConnect();
        }

    }


    /**
     * 事务完成归还链接
     * @throws SQLException
     */
    public static void releaseConnect() {
        //无论什么情况，所有的事务都需要回滚
        isTransactionActive.set(false);
        transactionCounter.set(0);
        //rollback the transaction
        try {
            Connection connection = connectionThreadLocal.get();
            connection.unwrap(java.sql.Connection.class).setAutoCommit(true);
            connection.setAutoCommit(true);

            //回归连接池
            connection.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws SQLException {
        return connectionThreadLocal.get();
    }
}
