package com.momoc.frame.orm.annotation;

import java.lang.reflect.Field;

public class AnnotationUtil {


    /**
     * 获取数据库的字段名称
     * @param field
     * @return
     */
    public static String getFieldName(Field field) {
        MiniEntityTableFieldName annotation = field.getAnnotation(MiniEntityTableFieldName.class);
        EntityID entityID = field.getAnnotation(EntityID.class);
        String tableFieldName = field.getName();
        if (entityID != null) {
            tableFieldName = entityID.name();
        } else if (annotation != null) {
            tableFieldName = annotation.name();
        }
        return tableFieldName;
    }
}
