import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.EntityPage;
import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.DBParam;
import lombok.extern.slf4j.Slf4j;
import model.TestTable;

import java.util.*;


@Slf4j
public class TestQuery {
    public static void main(String[] args) {

        HashMap<String, Object> paramsOne = new HashMap<>();
        paramsOne.put("id", 1);

        ArrayList<Integer> entities = new ArrayList<>();
        entities.add(1);
        entities.add(2);

        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);

//        Map<String, Object> two = baseEntityTemplateMapper.buildQueryMap();


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

        List<TestTable> queryBeanListByMap = baseEntityTemplateMapper.queryBeanListByMap( TestTable.class,new DBParam("age", 40),new DBParam("id", entities), new DBParam("name", "test2"));
        System.out.println("queryBeanListByMap:"  + queryBeanListByMap);

        System.out.println("====================");

        EntityPage<TestTable> testTableEntityPage = new EntityPage<>();
        baseEntityTemplateMapper.queryPageByMap( testTableEntityPage, new DBParam("id", entities));
        System.out.println("queryPageByMap:"  + testTableEntityPage);
        System.out.println("====================");

    }
}
