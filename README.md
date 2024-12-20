> 待做事项


1、 实现一个ORM 接口

CRUD  分页查询、批量插入

2、做一个ORM插件代码生成

3 支持Spring OR 手动初始化

4、支持特殊的原生连接池

5、数据库命名字段映射，驼峰 OR A_B

6、 支持事务



> 数据库的数据类型对应Java实体类

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