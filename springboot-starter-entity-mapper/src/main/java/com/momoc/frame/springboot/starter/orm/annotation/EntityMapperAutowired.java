package com.momoc.frame.springboot.starter.orm.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.*;

@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface EntityMapperAutowired {
}
