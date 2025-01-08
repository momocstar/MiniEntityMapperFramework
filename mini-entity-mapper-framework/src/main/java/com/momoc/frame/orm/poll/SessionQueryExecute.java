package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.ClassFieldTableMapperCache;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.processor.*;
import com.momoc.frame.orm.util.EntityMethodUtil;
import com.mysql.cj.PreparedQuery;
import com.mysql.cj.jdbc.ClientPreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
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
    public static List<Map<String, Object>> queryMapSql(StringBuilder sql, DBParam... dbParams) {
        Connection connection = null;
        try {
            connection = DatabaseConnectionPool.getConnection();

            NamedQueryPreparedStatementProcessor namedPreparedStatementProcessor = new NamedQueryPreparedStatementProcessor(connection, sql, dbParams);

            namedPreparedStatementProcessor.setObject(dbParams);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();

            ResultSet resultSet = preparedStatement.executeQuery();
//            String finalSql = sql.toString();
//            logger.debug("Final SQL: {}", finalSql);

            return EntityMethodUtil.queryRsToMap(resultSet);
        } catch (Exception e) {
            logger.error("Error executing query: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            DatabaseConnectionPool.close(connection);
        }

    }

//    public static <R> List<R> queryPageBeanSql(StringBuilder sql, List<DBParam> dbParams, Class<R> entityClass) {
//        return queryBeanSql(sql, dbParams, entityClass);
//    }




    public static <R> List<R> queryBeanSql(StringBuilder sql, Class<R> entityClass, DBParam... dbParams)  {
        Connection connection = null;
        List<R> ts;
        try {
            connection = DatabaseConnectionPool.getConnection();

            NamedQueryPreparedStatementProcessor namedPreparedStatementProcessor = new NamedQueryPreparedStatementProcessor(connection, sql, dbParams);
            namedPreparedStatementProcessor.setObject(dbParams);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery();
            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, ClassFieldTableMapperCache.buildFiledSetterMethodMap(entityClass));
            String finalSQL = ((PreparedQuery) preparedStatement.unwrap(ClientPreparedStatement.class).getQuery()).asSql();
            logger.debug("final statement sql:{}", finalSQL);
            return ts;
        } catch (Exception e) {
            logger.error("Error executing query: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            DatabaseConnectionPool.close(connection);
        }
    }


    public static <R> List<R> queryPageBeanSql(StringBuilder sql, Class<R> entityClass, EntityPage<R> entityPage,  DBParam... dbParams)  {
        Connection connection = null;
        List<R> ts;

        try {
            connection = DatabaseConnectionPool.getConnection();
            NamedPagePreparedStatementProcessor namedPreparedStatementProcessor = new NamedPagePreparedStatementProcessor(connection, sql,dbParams, entityPage);
            namedPreparedStatementProcessor.setObject(dbParams);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();
            ResultSet resultSet = preparedStatement.executeQuery();
            ts = EntityMethodUtil.queryRsToBean(resultSet, entityClass, ClassFieldTableMapperCache.buildFiledSetterMethodMap(entityClass));
            String finalSQL = ((PreparedQuery) preparedStatement.unwrap(ClientPreparedStatement.class).getQuery()).asSql();
            logger.debug("final statement sql:{}", finalSQL);
            return ts;
        } catch (Exception e) {
            logger.error("Error executing query: {}", sql, e);
            throw new RuntimeException(e);
        } finally {
            DatabaseConnectionPool.close(connection);
        }
    }


}
