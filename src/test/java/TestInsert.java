import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.mapper.DBParam;
import model.TestTable;

import java.util.Collections;

public class TestInsert {

    public static void main(String[] args) {
//
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);
//
        Long insert = baseEntityTemplateMapper.insert(new DBParam("name", "张三"), new DBParam("age", 256), new DBParam("sBigint", 10000L));
//
        System.out.println("insert = " + insert);


        TestTable testTable = new TestTable();
        testTable.setName("李四");
        testTable.setAge("21");
        testTable.setSBigInt(1000000L);

        Long insert1 = baseEntityTemplateMapper.insert(testTable);
//        System.out.println("insert = " + insert1);

        testTable.setSEnum("active");

        long[] longs = baseEntityTemplateMapper.batchEntityInsert(Collections.singletonList(testTable));
        System.out.println("insert = " + longs[0]);

        testTable.setId(14);
        testTable.setAge("91");
        baseEntityTemplateMapper.insertOnDuplicateUpdate(Collections.singletonList(testTable));
        testTable.setId(15);
        testTable.setAge("91");
        baseEntityTemplateMapper.insertOnDuplicateUpdate(Collections.singletonList(testTable), "age");


    }
}
