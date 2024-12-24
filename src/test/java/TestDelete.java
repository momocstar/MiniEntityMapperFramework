import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.mapper.DBParam;
import model.TestTable;

import java.util.ArrayList;
import java.util.Collections;

public class TestDelete {

    public static void main(String[] args) {
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);


        boolean b = baseEntityTemplateMapper.deleteById(26);
        System.out.println(b);
        System.out.println(baseEntityTemplateMapper.deleteByIds(Collections.singletonList(25)));


    }
}
