package com.momoc.frame.orm.proccessor;

import com.momoc.frame.orm.EntityPage;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Map;

public class NamedPageEntityPreparedProcessor extends NamedPreparedProcessor {

    private static NamedPageEntityPreparedProcessor instance;

    public static NamedPageEntityPreparedProcessor getInstance() {
        if (instance == null) {
            synchronized (NamedPageEntityPreparedProcessor.class) {
                if (null == instance) {
                    instance = new NamedPageEntityPreparedProcessor();
                }
                return instance;
            }
        }
        return instance;
    }


    /**
     * 处理sql  where的参数条件
     *
     * @param sql      sql
     * @param dbParams 参数
     * @return
     */
    public <R> ArrayList<Object> handlerWhereParams( StringBuilder sql, Map<String, Object> dbParams, EntityPage<R> entityPage) throws SQLException {
        if (entityPage == null) {
            throw new IllegalArgumentException("entityPage is null");
        }
        if (entityPage.getPage() == null ){
            entityPage.setPage(1);
        }
        if (entityPage.getPage() < 1 ){
            throw new IllegalArgumentException("entityPage page is less than 1");
        }
        if (entityPage.getPageSize() == null){
            entityPage.setPageSize(10);
        }
        if (entityPage.getPageSize() < 1 ){
            throw new IllegalArgumentException("entityPage page size is less than 1");
        }
        int skip = (entityPage.getPage() - 1) * entityPage.getPageSize();

        ArrayList<Object> list = super.handlerWhereParams(sql, dbParams);
        sql.append(" limit " + skip + " , " + entityPage.getPageSize());
        return list;
    }


}
