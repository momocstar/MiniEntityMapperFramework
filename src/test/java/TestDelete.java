import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import model.TestTable;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.Collections;

public class TestDelete {

    public static void main(String[] args) {
        /**
         * 默认写死了本地库,用于test
         */
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        /**
         * 如果使用了spring ，可以手动注入
         */
        DatabaseConnectionPool.initializingDataSource(dataSource);

        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);


        boolean b = baseEntityTemplateMapper.deleteById(26);
        System.out.println(b);
        System.out.println(baseEntityTemplateMapper.deleteByIds(Collections.singletonList(25)));
        System.out.println(baseEntityTemplateMapper.deleteEntities(new DBParam("age", 40)));


    }
}
