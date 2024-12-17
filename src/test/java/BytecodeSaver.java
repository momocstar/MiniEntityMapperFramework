import com.momoc.frame.orm.asm.AsmClassInfo;
import com.momoc.frame.orm.asm.EntityDynamicClassCreator;
import model.TestTable;

import java.io.FileOutputStream;
import java.io.IOException;

public class BytecodeSaver {

    public static void saveBytecode(byte[] bytecode, String filePath) {
        try (FileOutputStream fos = new FileOutputStream(filePath)) {
            fos.write(bytecode);
            System.out.println("Bytecode has been written to " + filePath);
        } catch (IOException e) {
            System.err.println("Error while writing bytecode to file: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        // 假设你已经有了字节码数组
        AsmClassInfo baseEntityImplClass = EntityDynamicClassCreator.createBaseEntityImplClass(TestTable.class, Integer.class);

        String className = baseEntityImplClass.getClassFullName();
        // 指定要保存字节码的文件路径
        String filePath = String.format("/Users/momoc/IdeaProjects/MyFrame/%s.class", baseEntityImplClass.getSimpleClassName());

        // 保存字节码到文件
        saveBytecode(baseEntityImplClass.getByteClassCode(), filePath);
    }
}