package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.proccessor.NamedPreparedStatementProcessor;
import com.momoc.frame.orm.transaction.EntityTransactionManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;

public class BaseSessionOperation {
    private static Logger logger = LoggerFactory.getLogger(BaseSessionOperation.class);

    public static boolean execute(StringBuilder sql, DBParam[] dbParams) {

        Connection connection = null;

        try {
            connection = DatabaseConnectionPool.getConnection();
            NamedPreparedStatementProcessor namedPreparedStatementProcessor = new NamedPreparedStatementProcessor(connection, sql, dbParams);
            namedPreparedStatementProcessor.setObject(dbParams);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();
            logger.debug("executing prepared statement: {}", preparedStatement);
            preparedStatement.execute();
            return true;
        } catch (Exception e) {
            logger.error("Error executing : ", e);
            throw new RuntimeException(e);
        } finally {
            DatabaseConnectionPool.close(connection);
        }
    }
}
