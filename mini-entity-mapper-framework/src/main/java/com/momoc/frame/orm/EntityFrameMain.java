package com.momoc.frame.orm;

import com.momoc.frame.orm.poll.DatabaseConnectionPool;

import javax.sql.DataSource;

public class EntityFrameMain {




    public static void init(DataSource dataSource){
        DatabaseConnectionPool.initializingDataSource(dataSource);
    }
}
