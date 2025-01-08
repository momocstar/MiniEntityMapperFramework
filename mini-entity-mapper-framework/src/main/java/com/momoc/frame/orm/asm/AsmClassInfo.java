package com.momoc.frame.orm.asm;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AsmClassInfo {


    /**
     * 字节码
     */
    byte[] byteClassCode;

    // 带有/
    String classFullName;


    /**
     * 所在包
     */
    String packageName;

    /**
     * 简称
     */
    String simpleClassName;

}
