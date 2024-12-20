package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.util.ArrayUtil;
import lombok.Getter;

import java.sql.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NamedPagePreparedStatementProcessor extends NamedPreparedStatementProcessor {

    EntityPage<?> entityPage;

    DBParam[] pageDbParams;

    /**
     * 在此方法设置? 参数的其实位置
     */
    Integer maxIndex;


    public NamedPagePreparedStatementProcessor(Connection connection, StringBuilder SQL, DBParam[] dbParams, EntityPage<?> entityPage) throws SQLException {
        super(connection, SQL, dbParams);
        this.entityPage = entityPage;
        validPage();
    }

    private void validPage() {
        if (entityPage == null) {
            throw new IllegalArgumentException("entityPage is null");
        }
        if (entityPage.getPage() == null) {
            entityPage.setPage(1);
        }
        if (entityPage.getPage() < 1) {
            throw new IllegalArgumentException("entityPage page is less than 1");
        }
        if (entityPage.getPageSize() == null) {
            entityPage.setPageSize(10);
        }
        if (entityPage.getPageSize() < 1) {
            throw new IllegalArgumentException("entityPage page size is less than 1");
        }
    }


    @Override
    protected Integer replacePlaceholder(StringBuilder sql, DBParam[] dbParams) {
        this.maxIndex = super.replacePlaceholder(sql, dbParams);
        return this.maxIndex;
    }

    public void setObject(DBParam... dbParams) throws SQLException {
        super.setObject(dbParams);
        /**
         * 额外处理分页参数
         */
        int skip = (entityPage.getPage() - 1) * entityPage.getPageSize();
        this.getPreparedStatement().setInt(this.maxIndex + 1, skip);
        this.getPreparedStatement().setInt(this.maxIndex + 2, entityPage.getPageSize());
    }


    @Override
    protected void preProcessing(StringBuilder parseSQL, DBParam[] dbParams) {
        parseSQL.append(" limit ?,?");
    }


}
