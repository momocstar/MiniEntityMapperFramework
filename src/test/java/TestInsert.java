import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import model.TestTable;

public class TestInsert {

    public static void main(String[] args) {
//
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);
//
//        Long insert = baseEntityTemplateMapper.insert(new DBParam("name", "张三"), new DBParam("age", 20), new DBParam("sBigint", 10000L));
//
//        System.out.println("insert = " + insert);


        TestTable testTable = new TestTable();
        testTable.setName("李四");
        testTable.setAge("20");
        testTable.setSBigInt(1000000L);

        Long insert1 = baseEntityTemplateMapper.insert(testTable);
        System.out.println("insert = " + insert1);


    }
}
