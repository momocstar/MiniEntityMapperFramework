package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.ClassFieldTableMapperCache;
import com.momoc.frame.orm.proccessor.NamedPageEntityPreparedProcessor;
import com.momoc.frame.orm.proccessor.NamedPreparedProcessor;
import com.momoc.frame.orm.proccessor.SqlParamsPreparedProcessor;
import com.momoc.frame.orm.util.EntityMethodUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * 查询sql
 */
public class SessionQueryExecute {


    private static Logger logger = LoggerFactory.getLogger(DatabaseConnectionPool.class);

    /**
     * 查询后将结果映射成map
     *
     * @param sql
     * @param dbParams
     * @return
     */
    public static List<Map<String, Object>> queryMapSql(StringBuilder sql, Map<String, Object> dbParams) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            ArrayList<Object> list = NamedPreparedProcessor.getInstance().handlerWhereParams(sql, dbParams);
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
                }
            }
        }

    }

    public static <R> List<R> queryPageBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass, EntityPage<R> entityPage) {
        return queryBeanSql(sql, dbParams, entityClass, entityPage, NamedPageEntityPreparedProcessor.getInstance());
    }

    public static <R> List<R> queryBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass, EntityPage<R> entityPage,
                                           SqlParamsPreparedProcessor paramsPreparedProcessor) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;
        List<R> ts;

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
            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, ClassFieldTableMapperCache.buildFiledSetterMethodMap(entityClass));
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
                }
            }
        }
    }

    public static <R> List<R> queryBeanSql(StringBuilder sql, Map<String, Object> dbParams, Class<R> entityClass) {
        //NamedPreparedProcessor不会使用entityPage
        return queryBeanSql(sql, dbParams, entityClass, null, NamedPreparedProcessor.getInstance());
    }
}
