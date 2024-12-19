package com.momoc.frame.orm.mapper;

import lombok.Data;

import java.util.Collection;

@Data
public class DBParams {


    /**
     * 查询参数
     * @param name 不需要带@,不支持s.XXX
     * @param value 值
     */

    public DBParams(String name, Object value) {
        this.name = name;
        this.value = value;
        if (value instanceof Collection) {
            this.collectionSize = ((Collection<?>) value).size();
        }
    }
    //表字段名称
    String name;
    //值
    Object value;
    //参数处理位置
    Integer position;

    /**
     * List集合的大小，有多少个就预处理多少个？
     */
    Integer collectionSize;
}
