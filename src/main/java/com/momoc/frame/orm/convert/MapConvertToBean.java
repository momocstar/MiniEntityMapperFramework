package com.momoc.frame.orm.convert;

import java.util.Map;

/**
 * 自定义类型转换扩展接口
 */
public interface MapConvertToBean<T> {


   /**
    *
    * 转换方法
    * @param dataRow 数据库获取的行数据
    * @return
    */
   T convertToBean(Map<String, Object> dataRow);
}
