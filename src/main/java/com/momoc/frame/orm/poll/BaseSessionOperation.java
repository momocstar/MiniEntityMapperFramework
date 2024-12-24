package com.momoc.frame.orm.poll;

import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.proccessor.NamedPreparedStatementProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;

public class BaseSessionOperation {
    private static Logger logger = LoggerFactory.getLogger(BaseSessionOperation.class);

    public static boolean execute(StringBuilder sql, DBParam[] dbParams) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            NamedPreparedStatementProcessor namedPreparedStatementProcessor = new NamedPreparedStatementProcessor(connection, sql, dbParams);
            namedPreparedStatementProcessor.setObject(dbParams);
            PreparedStatement preparedStatement = namedPreparedStatementProcessor.getPreparedStatement();
            logger.debug("executing prepared statement: {}", preparedStatement);
            return !preparedStatement.execute();
        } catch (Exception e) {
            logger.error("Error executing : ", e);
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
}
