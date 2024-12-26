import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import lombok.extern.slf4j.Slf4j;
import model.OrderDTO;
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


        HashMap<String, Object> paramsOne = new HashMap<>();
        paramsOne.put("id", 1);

        ArrayList<Integer> entities = new ArrayList<>();
        entities.add(1);
        entities.add(2);

        /**
         * 生成查询模板实例
         */
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);

        System.out.println("====================");
        System.out.println("=========test_order");
        OrderDTO testOrder = baseEntityTemplateMapper.queryBean("select u.id as userid, o.orderNo,o.id from t_user u join t_order o on u.id = o.userid ", OrderDTO.class, new DBParam("o.id", 1));
        System.out.println(testOrder);
//        Map<String, Object> two = baseEntityTemplateMapper.buildQueryMap();
        System.out.println("====================");


        TestTable testTable = baseEntityTemplateMapper.queryOneById(1);

        System.out.println("queryOneById:"  + testTable);

        System.out.println("====================");
        List<TestTable> testTables = baseEntityTemplateMapper.queryListByIds(entities);
        System.out.println("queryListByIds:"  + testTables);

        System.out.println("====================");



        TestTable queryOneByCondition = baseEntityTemplateMapper.queryOneByCondition(paramsOne);
        System.out.println("queryOneByCondition:"  + queryOneByCondition);

        System.out.println("====================");

        List<TestTable> queryListByMap = baseEntityTemplateMapper.queryListByMap(new DBParam("id", entities));
        System.out.println("queryListByMap:"  + queryListByMap);
        System.out.println("====================");

        Long i = baseEntityTemplateMapper.countByMap(new DBParam("id", entities));
        System.out.println("countByMap:"  + i);
        System.out.println("====================");

        TestTable queryBean = baseEntityTemplateMapper.queryBean( TestTable.class, paramsOne);
        System.out.println("queryBean:"  + queryBean);

        System.out.println("====================");

        List<TestTable> queryBeanListByMap = baseEntityTemplateMapper.queryBeanListByMap( TestTable.class,new DBParam("age", 20));
        System.out.println("queryBeanListByMap:"  + queryBeanListByMap);

        System.out.println("====================");

        EntityPage<TestTable> testTableEntityPage = new EntityPage<>();
        baseEntityTemplateMapper.queryPageByMap( testTableEntityPage, new DBParam("age", 20), new DBParam("id", entities), new DBParam("sBigInt", 10000));
        System.out.println("queryPageByMap:"  + testTableEntityPage);
        System.out.println("====================");

        baseEntityTemplateMapper.queryPageByMap( testTableEntityPage, new DBParam("id", entities), new DBParam("age", 20));
        System.out.println("queryPageByMap:"  + testTableEntityPage);
        System.out.println("====================");

        test2();
    }
    public static void test2(){
        HashMap<String, Object> paramsOne = new HashMap<>();
        paramsOne.put("id", 1);

        ArrayList<Integer> entities = new ArrayList<>();
        entities.add(1);
        entities.add(2);

        BaseEntityTemplateMapper<TestTable2, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable2.class, Integer.class);

//        Map<String, Object> two = baseEntityTemplateMapper.buildQueryMap();


        TestTable2 testTable = baseEntityTemplateMapper.queryOneById(1);

        System.out.println("queryOneById:"  + testTable);

        System.out.println("====================");
        List<TestTable2> testTables = baseEntityTemplateMapper.queryListByIds(entities);
        System.out.println("queryListByIds:"  + testTables);

        System.out.println("====================");



        TestTable2 queryOneByCondition = baseEntityTemplateMapper.queryOneByCondition(paramsOne);
        System.out.println("queryOneByCondition:"  + queryOneByCondition);

        System.out.println("====================");

        List<TestTable2> queryListByMap = baseEntityTemplateMapper.queryListByMap(new DBParam("id", entities));
        System.out.println("queryListByMap:"  + queryListByMap);
        System.out.println("====================");

        Long i = baseEntityTemplateMapper.countByMap(new DBParam("id", entities));
        System.out.println("countByMap:"  + i);
        System.out.println("====================");

        TestTable2 queryBean = baseEntityTemplateMapper.queryBean( TestTable2.class, paramsOne);
        System.out.println("queryBean:"  + queryBean);

        System.out.println("====================");

        List<TestTable2> queryBeanListByMap = baseEntityTemplateMapper.queryBeanListByMap( TestTable2.class,new DBParam("age", 20));
        System.out.println("queryBeanListByMap:"  + queryBeanListByMap);

        System.out.println("====================");

        EntityPage<TestTable2> testTableEntityPage = new EntityPage<>();
        baseEntityTemplateMapper.queryPageByMap( testTableEntityPage, new DBParam("age", 20));
        System.out.println("queryPageByMap:"  + testTableEntityPage);
        System.out.println("====================");
    }
}
