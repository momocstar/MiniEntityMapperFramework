package model;

import com.momoc.frame.orm.annotation.EntityID;
import com.momoc.frame.orm.annotation.MiniEntityTableFieldName;
import com.momoc.frame.orm.annotation.MiniEntityTableName;
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
