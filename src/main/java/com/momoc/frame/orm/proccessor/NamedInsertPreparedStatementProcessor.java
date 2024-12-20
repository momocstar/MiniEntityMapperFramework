package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.mapper.DBParam;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;



public class NamedInsertPreparedStatementProcessor extends NamedPreparedStatementProcessor {

    public NamedInsertPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams) throws SQLException {
        super(connection, SQL, dbParams, Statement.RETURN_GENERATED_KEYS);

    }

    public NamedInsertPreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams, Integer RETURN_GENERATED_KEYS) throws SQLException {
        super(connection, SQL, dbParams, RETURN_GENERATED_KEYS);
    }
}
