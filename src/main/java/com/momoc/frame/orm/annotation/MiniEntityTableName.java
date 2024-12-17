package com.momoc.frame.orm.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * 标记数据库表名
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface MiniEntityTableName {
    String name() default "";
}
