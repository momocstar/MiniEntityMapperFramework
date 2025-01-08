### 项目简介

无事，悠闲，只想敲下代码

本项目不依赖spring，轻量JAVA ORM工具。欢迎大家一起完善













### 实现技术点

ASM、模板模式、自定义类加载器、反射

### 注解说明





> 标识数据库ID -> EntityID

通过ID查询删除、更新操作使用

不标识，默认取id为主键



```java
package com.momoc.frame.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 数据库表的ID字段名称
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface EntityID {
    String name();
}
```

> 字段名称 -> MiniEntityTableFieldName

用于标识数据库的字段名称，也可用于标识select查询中的表字段名对应实体的字段。不写默认同名

```java
package com.momoc.frame.orm.annotation;


import java.lang.annotation.*;

/**
 * 表的字段列名称
 */
@Inherited
@Target({ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface MiniEntityTableFieldName{
    String name();
}

```



> 表名称 ->  用于标识实体对应的表名称

用于标识数据库的字段名称，也可用于标识select查询中的表字段名对应实体的字段。不写默认同名

```java
package com.momoc.frame.orm.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 标记数据库表名
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.TYPE})
public @interface MiniEntityTableName {
    String name();
}

```







### spring boot 使用教程



引入starter启动项目即可

```xml
<dependency>
    <groupId>com.momoc.frame</groupId>
    <artifactId>springboot-starter-entity-mapper</artifactId>
    <version>0.0.1-SNAPSHOT</version>
</dependency>
```





> 自动注入

```java
package com.momoc.frame.springbootstarterentitymappertest;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.springboot.starter.orm.annotation.EntityMapperAutowired;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable2;
import lombok.Getter;
import org.springframework.stereotype.Service;

@Service
public class TestService {

    @Getter
    @EntityMapperAutowired
    BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper;
}
```



> 继承后交给spring自动管理、注入

```java
package com.momoc.frame.springbootstarterentitymappertest;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.springbootstarterentitymappertest.model.TestTable;
import org.springframework.stereotype.Service;


@Service
public class TestTableMapper extends BaseEntityTemplateMapper<TestTable, Integer> {
    
    
}



@Service
public class TestService {

    @Getter
    @EntityMapperAutowired
    BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper;

    @Autowired
    TestTableMapper testTableMapper;

}

```



### 手动初始化，初始化后就可以使用模板查询了

```java

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import lombok.extern.slf4j.Slf4j;
import model.TestTable;
import model.TestTable2;

import javax.sql.DataSource;
import java.util.*;


@Slf4j
public class TestQuery {
    public static void main(String[] args) {
           /**
         * 默认写死了本地库,用于test
         */
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        /**
         * 如果使用了spring ，可以手动注入dataSource
         */
        DatabaseConnectionPool.initializingDataSource(dataSource);


        /**
         * 生成查询模板实例
         */
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);

//        Map<String, Object> two = baseEntityTemplateMapper.buildQueryMap();


        TestTable testTable = baseEntityTemplateMapper.queryOneById(1);
    }
   
}

```



### 根据模板类，使用ASM生成的子类---反编译

```java
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.momoc.frame.orm.mapper.generate;

import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import model.TestTable;

public class MiniEntityMapperEnhancerIntegerAms$f840b0ed extends BaseEntityTemplateMapper<TestTable, Integer> {
    public MiniEntityMapperEnhancerIntegerAms$f840b0ed(Class<TestTable> var1, Class<Integer> var2) {
        super(var1, var2);
    }
}

```



### 模板类相关实现

[模板类代码](./MiniEntityMapperFramework/mini-entity-mapper-framework/src/main/java/com/momoc/frame/orm/mapper/BaseEntityTemplateMapper.java)

#### 模板类特殊使用说明



如一下方法，返回值带有R的，可以定义对应的VO或DTO类，可用于连表查询，手动处理字段与实体的映射关系。

```java
<R> R queryBean(String sql, Class<R> RClass, DBParam... dbParams);
```



如实体字段，如下，订单数据DTO，有用户和订单表的实体字段

```java
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

```

调用

```java
OrderDTO testOrder = baseEntityTemplateMapper.queryBean("select u.id as userid, o.orderNo,o.id from t_user u join t_order o on u.id = o.userid ", OrderDTO.class, new DBParam("o.id", 1));

```











### 待做事项


1、 实现一个ORM 接口

CRUD  分页查询、批量插入

2、做一个ORM插件代码生成（待做）

3 支持Spring OR 手动初始化, 创建一个autoConfig项目

4、传入dataSource对象即可初始化

5、数据库命名字段映射，驼峰 OR A_B

6、 支持事务，目前已支持编程式事务

7、数据表关系生成controller，低代码（妄想）



### 数据库的数据类型对应Java实体类

MySQL数据库中的各种数据类型映射到Java中的对象并不是一一对应的，因为Java是一种强类型语言，而MySQL是一种关系型数据库管理系统。不同的Java数据库连接技术（如JDBC、JPA、Hibernate等）可能会以不同的方式处理这些映射。以下是一些常见的MySQL数据类型及其在Java中对应的一般类型：

1. **BIT**：`boolean` 或 `byte[]`（取决于位数和JDBC驱动）

2. **TINYINT**（有符号）：`byte`

3. **TINYINT UNSIGNED**（无符号）：`short`

4. **SMALLINT**（有符号）：`short`

5. **SMALLINT UNSIGNED**（无符号）：`Integer`

6. **MEDIUMINT**（有符号）：`int`

7. **MEDIUMINT UNSIGNED**（无符号）：`Integer`

8. **INT** 或 **INTEGER**（有符号）：`int` 或 `Integer`

9. **INT UNSIGNED** 或 **INTEGER UNSIGNED**（无符号）：`long` 或 `Long`

10. **BIGINT**（有符号）：`long` 或 `Long`

11. **BIGINT UNSIGNED**（无符号）：`BigInteger`（Java 5+）

12. **FLOAT**：`float` 或 `Float`

13. **DOUBLE**：`double` 或 `Double`

14. **DECIMAL** 或 **NUMERIC**：`BigDecimal`

15. **CHAR** 和 **VARCHAR**：`String`

16. **TEXT**：`String` 或 `byte[]`（取决于内容和JDBC驱动）

17. **TINYTEXT**：`String`

18. **MEDIUMTEXT**：`String`

19. **LONGTEXT**：`String`

20. **BINARY** 和 **VARBINARY**：`byte[]`

21. **BLOB**：`byte[]` 或 `Blob`（Java SQL `Blob` 类型）

22. **TINYBLOB**：`byte[]`

23. **MEDIUMBLOB**：`byte[]`

24. **LONGBLOB**：`byte[]`

25. **DATE**：`java.sql.Date` 或 `LocalDate`（Java 8+）

26. **TIME**：`java.sql.Time` 或 `LocalTime`（Java 8+）

27. **DATETIME** 和 **TIMESTAMP**：`java.sql.Timestamp` 或 `LocalDateTime`（Java 8+）

28. **YEAR**：`int` 或 `java.sql.Date`（取决于具体实现）

29. **ENUM** 和 **SET**：`String` 或自定义类（取决于具体实现）

30. **GEOMETRY**：`byte[]` 或特定于供应商的类（取决于空间数据库扩展）

请注意，这些映射可能会根据您使用的JDBC驱动程序、ORM框架或Java版本有所不同。例如，Java 8引入了新的日期和时间API（如`LocalDate`、`LocalTime`、`LocalDateTime`等），这些API提供了比旧的`java.sql`类更好的日期和时间处理能力。在使用JDBC时，通常需要使用`ResultSet`的`getObject`方法，并指定适当的类类型来获取这些值。在使用ORM框架时，框架通常会处理这些映射的细节。



IDEA插件功能

链接数据库后，可以搜索表结构。生成Java类