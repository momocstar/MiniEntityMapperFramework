import com.momoc.frame.orm.BaseEntityTemplateMapper;
import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import model.TestTable;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;

public class Test {
    public static void main(String[] args) {
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);
        TestTable testTable = baseEntityTemplateMapper.queryOneById(1);
        List<TestTable> testTables = baseEntityTemplateMapper.queryListByIds(Collections.singletonList(1));

        System.out.println(testTable);
        System.out.println(testTables);

        HashMap<String, Object> params = new HashMap<>();
        params.put("id", 1);


        Long i = baseEntityTemplateMapper.countByMap(params);
        System.out.println(i);
    }
}
