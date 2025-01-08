package com.momoc.frame.springboot.starter.orm;

import com.momoc.frame.orm.config.MiniEntityMapperConfig;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import com.momoc.frame.springboot.starter.orm.proccessor.CreateEntityDynamicBeanProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import javax.sql.DataSource;

@Configuration
@AutoConfiguration
//@ConditionalOnClass({MiniEntityMapperConfig.class})
//@AutoConfigureAfter({DataSourceAutoConfiguration.class})
@Import({CreateEntityDynamicBeanProcessor.class, BaseMapperReferenceInject.class})
public class MiniEntityMapperAutoConfigure {
    private static final Logger LOGGER = LoggerFactory.getLogger(MiniEntityMapperAutoConfigure.class);

    public MiniEntityMapperAutoConfigure() {
    }


    @Bean
    @ConditionalOnMissingBean
    public DatabaseConnectionPool initDatabaseConnectionPool(DataSource dataSource) {
        LOGGER.info("init MiniEntityMapperAutoConfigure");
        return new DatabaseConnectionPool(dataSource);
    }
}
