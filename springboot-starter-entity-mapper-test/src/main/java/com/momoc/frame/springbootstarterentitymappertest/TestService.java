package com.momoc.frame.springbootstarterentitymappertest;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.springboot.starter.orm.annotation.EntityMapperAutowired;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable2;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Getter
    @EntityMapperAutowired
    BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper;
    @Getter
    @EntityMapperAutowired
    BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper2;

    @Autowired
    TestTableMapper testTableMapper;

}
