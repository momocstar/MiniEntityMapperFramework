package com.momoc.frame.orm.asm;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AsmClassInfo {

    byte[] byteClassCode;

    //带有/
    String classFullName;


    String packageName;

    String simpleClassName;

}
