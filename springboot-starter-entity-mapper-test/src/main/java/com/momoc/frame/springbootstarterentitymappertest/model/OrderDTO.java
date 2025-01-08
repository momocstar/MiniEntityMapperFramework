package com.momoc.frame.springbootstarterentitymappertest.model;

import lombok.Data;


/**
 * 数据库映射到对应Java实体的类型
 */
@Data
public class OrderDTO {


    Integer id;

    String orderNo;

    Integer userid;

}
