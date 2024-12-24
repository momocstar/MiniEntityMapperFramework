package com.momoc.frame.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 数据库表的ID字段名称
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface EntityID {
    String name() default "";
}
