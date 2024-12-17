package com.momoc.frame.orm.annotation;


import java.lang.annotation.*;

/**
 * 表的字段列名称
 */
@Target({ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniEntityTableFieldName {
    String name() default  "";
}
