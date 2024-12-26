import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import model.TestTable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;

public class TestUpdate {

    public static void main(String[] args) {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        DatabaseConnectionPool.initializingDataSource(dataSource);
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);

        boolean b = baseEntityTemplateMapper.updateById(21, new DBParam("age", "22"));
        System.out.println(b);

        TestTable testTable = baseEntityTemplateMapper.queryOneById(21);
        testTable.setAge("33");

        TestTable testTable2 = baseEntityTemplateMapper.queryOneById(22);
        testTable2.setAge("33");

        ArrayList<TestTable> testTables = new ArrayList<TestTable>() {{
            add(testTable);
            add(testTable2);
        }};
         baseEntityTemplateMapper.update(testTables);


//        baseEntityTemplateMapper.queryBean()
//        TestTable testTable = new TestTable();
//        testTable.setName("李四");
//        testTable.setAge("21");
//        testTable.setSBigInt(1000000L);
//
//        Long insert1 = baseEntityTemplateMapper.insert(testTable);
////        System.out.println("insert = " + insert1);
//
//        testTable.setSEnum("active");
//
//        long[] longs = baseEntityTemplateMapper.batchEntityInsert(Collections.singletonList(testTable));
//        System.out.println("insert = " + longs[0]);
//
//        testTable.setId("14");
//        testTable.setAge("91");
//        baseEntityTemplateMapper.insertOnDuplicateUpdate(Collections.singletonList(testTable));
//        testTable.setId("15");
//        testTable.setAge("91");
//        baseEntityTemplateMapper.insertOnDuplicateUpdate(Collections.singletonList(testTable), "age");


    }
}
