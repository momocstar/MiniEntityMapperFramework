package com.momoc.frame.orm.util;

import com.momoc.frame.orm.mapper.DBParam;

public class ArrayUtil {
    /**
     * 两个数组合并成一个，返回新的数组,浅拷贝
     * @param originalArray 旧数据
     * @param newElements 新内容
     * @return
     */
    public static DBParam[] addElements(DBParam[] originalArray, DBParam[] newElements) {
        // 创建一个新的数组，长度为原始数组加上新元素的数量
        DBParam[] newArray = new DBParam[originalArray.length + newElements.length];

        // 复制原始数组的内容到新数组
        System.arraycopy(originalArray, 0, newArray, 0, originalArray.length);

        // 将新元素复制到新数组
        System.arraycopy(newElements, 0, newArray, originalArray.length, newElements.length);

        return newArray;
    }
}
