import com.momoc.frame.orm.asm.EntityDynamicClassLoader;
import com.momoc.frame.orm.mapper.BaseEntityTemplateMapper;
import com.momoc.frame.orm.mapper.DBParam;
import com.momoc.frame.orm.poll.DatabaseConnectionPool;
import com.momoc.frame.orm.transaction.EntityTransactionManager;
import lombok.extern.slf4j.Slf4j;
import model.TestTable;

import javax.sql.DataSource;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.sql.SQLException;

@Slf4j
public class TestTransation {
    public static void main(String[] args) throws IOException {
        DataSource dataSource = DatabaseConnectionPool.getDataSource();
        DatabaseConnectionPool.initializingDataSource(dataSource);
        BaseEntityTemplateMapper<TestTable, Integer> baseEntityTemplateMapper = EntityDynamicClassLoader.generateMapperTemplateClass(TestTable.class, Integer.class);


        try {
            EntityTransactionManager.startTransaction();
            TestTable testTable = baseEntityTemplateMapper.queryOneById(30);


            baseEntityTemplateMapper.updateById(testTable.getId(), new DBParam("age", 20));
            testTable.setAge("41");
            baseEntityTemplateMapper.update(testTable);
            EntityTransactionManager.commitTransaction();
        } catch (Exception e) {
            log.debug("commit error",e);
            EntityTransactionManager.rollbackTransaction();

        }


    }
}
