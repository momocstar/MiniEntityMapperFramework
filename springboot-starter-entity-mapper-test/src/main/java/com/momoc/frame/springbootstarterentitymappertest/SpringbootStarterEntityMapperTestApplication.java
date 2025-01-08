package com.momoc.frame.springbootstarterentitymappertest;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.springboot.starter.orm.MiniEntityMapperAutoConfigure;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

import javax.sql.DataSource;

@SpringBootApplication
public class SpringbootStarterEntityMapperTestApplication {



    public static void main(String[] args) {
        ConfigurableApplicationContext run = SpringApplication.run(SpringbootStarterEntityMapperTestApplication.class, args);
        DataSource dataSource = run.getBean(DataSource.class);
        System.out.println(dataSource);
        MiniEntityMapperAutoConfigure bean = run.getBean(MiniEntityMapperAutoConfigure.class);
        System.out.println(bean);

        TestService bean1 = run.getBean(TestService.class);
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = bean1.getBaseEntityTemplateMapper();
        System.out.println(baseEntityTemplateMapper);
        System.out.println(bean1.getBaseEntityTemplateMapper2());
        TestTable testTable = baseEntityTemplateMapper.queryOneById(3);
        System.out.println(testTable);
        System.out.println(dataSource);
//        System.out.printf(String.valueOf(baseEntityTemplateMapper));
    }


}
