package com.momoc.frame.orm.convert;

import java.util.Map;

/**
 * 自定义类型转换扩展接口
 */
public interface MapConvertToBean<T> {


   T convertToBean(Map<String, Object> dataRow);
}
