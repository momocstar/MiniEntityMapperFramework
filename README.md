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







### 使用

手动初始化，初始化后就可以使用模板查询了

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









```java
package com.momoc.frame.orm.mapper;

import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.convert.MapConvertToBean;

import java.util.*;

import com.momoc.frame.orm.poll.BaseSessionOperation;
import com.momoc.frame.orm.poll.SessionQueryExecute;
import com.momoc.frame.orm.poll.SessionUpdateOrInsertExecute;
import lombok.Getter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public abstract class BaseEntityTemplateMapper<T, E> implements BaseEntityQueryMapper<T, E>, BaseEntityInsertMapper<T>, BaseEntityUpdateMapper<T, E> ,BaseEntityDeleteMapper<E>{


    private final Logger logger = LoggerFactory.getLogger(BaseEntityTemplateMapper.class);

    public BaseEntityTemplateMapper(Class<T> entityClass, Class<E> eClass) {
        this.entityClass = entityClass;
        allTableQueryField = this.getAllTableQueryField(entityClass);
        this.eClass = eClass;
        this.tableName = this.getTableName(entityClass);
        this.IDName = this.getIDName(entityClass);
    }


    /**
     * 实体类Class
     */
    protected Class<T> entityClass;


    /**
     * 主键类Class
     */
    protected Class<E> eClass;

    protected String IDName;


    /**
     * 表名
     */
    protected String tableName;

    /**
     * 字段与映射
     */
//    public Map<String, Method> tableFieldNameSetterMap = new HashMap<String, Method>();


    @Getter
    protected String allTableQueryField;

    @Override
    public T queryOneById(E id) {
        StringBuilder sql = new StringBuilder(allTableQueryField + " where ");

        sql.append(IDName).append(" = ").append("@").append(IDName).append("  limit 1");

        List<T> ts = SessionQueryExecute.queryBeanSql(sql, this.entityClass, (new DBParam(IDName, id)));
        return ts.isEmpty() ? null : ts.get(0);


    }


    @Override
    public List<T> queryListByIds(Collection<E> ids) {
        StringBuilder sql = new StringBuilder(allTableQueryField);
        List<T> ts = SessionQueryExecute.queryBeanSql(sql, this.entityClass, new DBParam(IDName, ids));
        return ts;
    }

    /**
     * @param params 字段参数,支持基本数据类型和List
     * @return
     */
    @Override
    public T queryOneByCondition(Map<String, Object> params) {
        return queryOneByCondition(allTableQueryField, params);
    }

    @Override
    public T queryOneByCondition(String sql, Map<String, Object> params) {
        return queryOneByCondition(sql, this.buildQueryDBParams(params));

    }

    @Override
    public List<T> queryListByMap(Map<String, Object> params) {
        return queryListByMap(allTableQueryField, params);
    }

    @Override
    public List<T> queryListByMap(String sql, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        return queryListByMap(allTableQueryField, this.buildQueryDBParams(params));

    }

    @Override
    public Long countByMap(Map<String, Object> params) {
        return countByMap("select count(1) from " + this.tableName, params);
    }


    @Override
    public Long countByMap(String sql, Map<String, Object> params) {
        return countByMap(sql, this.buildQueryDBParams(params));
    }

    @Override
    public <R> R queryBean(Class<R> RClass, Map<String, Object> params) {
        String queryFieldSql = SQLFieldGenerate.getAllTableQueryField(RClass);

        return queryBean(queryFieldSql, RClass, this.buildQueryDBParams(params));

    }


    @Override
    public <R> R queryBean(String sql, Class<R> RClass, Map<String, Object> params) {
        List<R> ts = queryBeanListByMap(sql, RClass, this.buildQueryDBParams(params));
        return ts.isEmpty() ? null : ts.get(0);
    }

    //    @Override
//    public <R> List<R> queryBeanListByMap(String sql, Map<String, Object> params, MapConvertToBean<R> convertToBean) {
//
//        return ts;
//    }
    @Override
    public <R> List<R> queryBeanListByMap(Class<R> RClass, Map<String, Object> params) {
        String tableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryBeanListByMap(tableQueryField, RClass, params);
    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return null;
        }
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, RClass, this.buildQueryDBParams(params));
    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return Collections.emptyList();
        }

        return queryBeanListByMap(sql, convertToBean, this.buildQueryDBParams(params));
    }

    private DBParam[] buildQueryDBParams(Map<String, Object> params) {
        DBParam[] dbParams = new DBParam[params.size()];
        int i = 0;
        for (Map.Entry<String, Object> entry : params.entrySet()) {
            dbParams[i++] = new DBParam(entry.getKey(), entry.getValue());
        }
        return dbParams;
    }


    @Override
    public EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, Map<String, Object> params) {
        return queryPageByMap(allTableQueryField, tEntityPage, params);
    }

    @Override
    public EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, Map<String, Object> params) {
        return queryPageByMap(sql, this.entityClass, tEntityPage, params);
    }


    @Override
    public <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, DBParam... dbParams) {
        String from = sql.substring(sql.indexOf("from"));
        Long total = countByMap("select count(*) " + from, dbParams);
        entityPage.setTotal(total);
        StringBuilder sqlBD = new StringBuilder(sql);

        List<R> ts = SessionQueryExecute.queryPageBeanSql(sqlBD, RClass, entityPage, dbParams);
        entityPage.setPageData(ts);
        return entityPage;
    }


    @Override
    public <R> EntityPage<R> queryPageByMap(String sql, Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params) {
        return queryPageByMap(sql, RClass, entityPage, this.buildQueryDBParams(params));
    }

    @Override
    public <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, Map<String, Object> params) {
        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryPageByMap(allTableQueryField, RClass, entityPage, params);
    }

    public <R> EntityPage<R> queryPageByMap(Class<R> RClass, EntityPage<R> entityPage, DBParam... dbParams) {

        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryPageByMap(allTableQueryField, RClass, entityPage, dbParams);
    }

//    public Map<String, Object> buildQueryMap(DBParam... dbParams) {
//        HashMap<String, Object> queryMap = new HashMap<>();
//        for (DBParam dbParam : dbParams) {
//            queryMap.put(dbParam.getName(), dbParam.getValue());
//        }
//        return queryMap;
//    }

    @Override
    public EntityPage<T> queryPageByMap(EntityPage<T> tEntityPage, DBParam... dbParams) {
        return queryPageByMap(this.entityClass, tEntityPage, dbParams);
    }

    @Override
    public EntityPage<T> queryPageByMap(String sql, EntityPage<T> tEntityPage, DBParam... dbParams) {
        return queryPageByMap(sql, this.entityClass, tEntityPage, dbParams);

    }

    @Override
    public T queryOneByCondition(DBParam... dbParams) {
        return queryOneByCondition(allTableQueryField, dbParams);
    }

    @Override
    public T queryOneByCondition(String sql, DBParam... dbParams) {
        List<T> ts = queryListByMap(sql, dbParams);
        return ts.isEmpty() ? null : ts.get(0);
//        return queryOneByCondition(sql, this.buildQueryMap(dbParams));
    }

    @Override
    public List<T> queryListByMap(DBParam... dbParams) {
        return queryListByMap(allTableQueryField, dbParams);
    }

    @Override
    public List<T> queryListByMap(String sql, DBParam... dbParams) {
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, this.entityClass, dbParams);

    }

    @Override
    public Long countByMap(DBParam... dbParams) {
        return countByMap("select count(*) from " + tableName, dbParams);
    }

    @Override
    public Long countByMap(String sql, DBParam... dbParams) {
        List<Map<String, Object>> maps = SessionQueryExecute.queryMapSql(new StringBuilder(sql), dbParams);

        if (!maps.isEmpty()) {
            Map<String, Object> dataRow = maps.get(0);
            if (!dataRow.isEmpty()) {
                for (Object value : dataRow.values()) {
                    return (Long) value;
                }
            }
        }
        return null;
    }

    @Override
    public <R> R queryBean(String sql, Class<R> RClass, DBParam... dbParams) {
        List<R> ts = queryBeanListByMap(sql, RClass, dbParams);
        return ts == null || ts.isEmpty() ? null : ts.get(0);
    }


    @Override
    public <R> List<R> queryBeanListByMap(String sql, MapConvertToBean<R> convertToBean, DBParam... dbParams) {


        ArrayList<R> rs = new ArrayList<>();
        List<Map<String, Object>> maps = SessionQueryExecute.queryMapSql(new StringBuilder(sql), dbParams);
        for (Map<String, Object> map : maps) {
            R r = convertToBean.convertToBean(map);
            rs.add(r);
        }
        return rs;

    }

    @Override
    public <R> List<R> queryBeanListByMap(String sql, Class<R> RClass, DBParam... dbParams) {
        StringBuilder sqlBD = new StringBuilder(sql);
        return SessionQueryExecute.queryBeanSql(sqlBD, RClass, dbParams);

    }

    @Override
    public <R> List<R> queryBeanListByMap(Class<R> RClass, DBParam... dbParams) {

        String allTableQueryField = SQLFieldGenerate.getAllTableQueryField(RClass);
        return queryBeanListByMap(allTableQueryField, RClass, dbParams);
//        return List.of();
    }

    @Override
    public Long insert(DBParam... dbParams) {
        StringBuilder sql = SQLFieldGenerate.generaInsertSQL(tableName, dbParams);
        return insertBySQL(sql, dbParams);
    }

    @Override

    public Long insertBySQL(StringBuilder sql, DBParam... dbParams) {
        return SessionUpdateOrInsertExecute.execute(sql, dbParams);
    }


    @Override
    public Long insertBySQL(String sql, DBParam... dbParams) {
        return SessionUpdateOrInsertExecute.execute(new StringBuilder(sql), dbParams);
    }

    @Override
    public Long insert(T entity) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, entity);
        DBParam[] array = entityInsertSQLFieldGenerate.getDbParamList().toArray(new DBParam[0]);
        return insertBySQL(entityInsertSQLFieldGenerate.getSQL(), array);
    }

    @Override
    public long[] batchEntityInsert(Collection<T> entities) {

        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass);

        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }

        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }

    @Override
    public long[] insertOnDuplicateUpdate(Collection<T> entities) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass, true);

        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }

        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }


    @Override
    public long[] insertOnDuplicateUpdate(Collection<T> entities, String... fields) {
        EntityInsertSQLFieldGenerate entityInsertSQLFieldGenerate = new EntityInsertSQLFieldGenerate(tableName, this.entityClass, fields);
        ArrayList<DBParam[]> dbParams = new ArrayList<>();
        for (T entity : entities) {
            DBParam[] dbParamsArr = entityInsertSQLFieldGenerate.buildEachParam(entity);
            dbParams.add(dbParamsArr);
        }
        return SessionUpdateOrInsertExecute.batchExecute(new StringBuilder(entityInsertSQLFieldGenerate.getSQL()), dbParams);
    }


    @Override
    public boolean updateById(E id, DBParam... dbParams) {
        DBParam dbParam = new DBParam(IDName, id);

        EntityUpdateSQLFieldGenerate entityUpdateSQLFieldGenerate = new EntityUpdateSQLFieldGenerate(tableName,  new DBParam[]{dbParam}, dbParams);
        long[] rs = SessionUpdateOrInsertExecute.batchExecute(Collections.singletonList(entityUpdateSQLFieldGenerate));
        return rs.length > 0 && rs[0] > 0;
    }


    @Override
    public long[] update(List<T> entities) {
        return update(entities, false);
    }

    @Override
    public boolean update(T entity) {

        return this.update(entity, false);
    }

    @Override
    public long[] update(List<T> entities, Boolean updateNUll) {
        ArrayList<IBatchExecute> iBatchExecutes = new ArrayList<>(entities.size());
        for (T entity : entities) {
            EntityUpdateSQLFieldGenerate entityUpdateSQLFieldGenerate = new EntityUpdateSQLFieldGenerate(tableName, entity, updateNUll);
            iBatchExecutes.add(entityUpdateSQLFieldGenerate);
        }
        return SessionUpdateOrInsertExecute.batchExecute(iBatchExecutes);
    }

    @Override
    public boolean updateBySQL(String sql, DBParam... dbParams) {
        EntityUpdateSQLFieldGenerate entityUpdateSQLFieldGenerate = new EntityUpdateSQLFieldGenerate(sql, dbParams);
        long[] rs = SessionUpdateOrInsertExecute.batchExecute(Collections.singletonList(entityUpdateSQLFieldGenerate));
        return rs.length > 0 && rs[0] > 0;
    }

    @Override
    public boolean update(T entity, Boolean updateNUll) {
        EntityUpdateSQLFieldGenerate entityUpdateSQLFieldGenerate = new EntityUpdateSQLFieldGenerate(tableName, entity, updateNUll);
        long[] rs = SessionUpdateOrInsertExecute.batchExecute(Collections.singletonList(entityUpdateSQLFieldGenerate));
        return rs.length > 0 && rs[0] > 0;
    }


    @Override
    public boolean deleteById(E id) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableName) .append( " where ").append(IDName).append(" = @id");
        return  BaseSessionOperation.execute(sql, new DBParam[]{new DBParam("id", id)});
    }

    @Override
    public boolean deleteByIds(List<E> ids) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableName) .append( " where ").append(IDName).append(" in (@ids)");
        return  BaseSessionOperation.execute(sql, new DBParam[]{new DBParam("ids", ids)});
    }

    @Override
    public boolean deleteEntities(DBParam... dbParams) {
        StringBuilder sql = new StringBuilder();
        sql.append("delete from ").append(tableName) .append( " where ");
        for (DBParam dbParam : dbParams) {
            if (dbParam.isCollection()){
                sql.append(dbParam.getName()).append(" in (@").append(dbParam.getName()).append(") ");
            }else{
                sql.append(dbParam.getName()).append(" = @").append(dbParam.getName()).append(" ");
            }
        }
        return BaseSessionOperation.execute(sql, dbParams);
    }
}

```





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