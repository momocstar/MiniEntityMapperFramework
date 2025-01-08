package com.momoc.frame.springbootstarterentitymappertest.model;

import com.momoc.frame.orm.annotation.EntityID;
import com.momoc.frame.orm.annotation.MiniEntityTableName;
import lombok.Data;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Date;

/**
 * 数据库映射到对应Java实体的类型
 */
@MiniEntityTableName(name = "TestTable")
@Data
public class TestTable2 {

    @EntityID(name = "id")
    String test;

    String name;

    String age;

    Long sBigInt;

    byte[] sBinary;

    byte[] sBit;

    String sChar;

    BigDecimal nDecimal;


    Date sDate;

    LocalDateTime sDateTime;

    String sEnum;

    Float nFloat;

    Integer nTinyint;


    String sTinyText;

    Date nYear;

    byte[] sVarBinary;
    /**
     * CREATE TABLE `TestTable` (
     *   `id` int NOT NULL AUTO_INCREMENT,
     *   `name` varchar(255) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `age` int DEFAULT NULL,
     *   `sBigInt` bigint DEFAULT NULL,
     *   `sBinary` binary(8) DEFAULT NULL,
     *   `sBit` bit(8) DEFAULT NULL,
     *   `sBlob` blob COMMENT '文件？',
     *   `sChar` char(36) COLLATE utf8mb4_general_ci DEFAULT NULL,
     *   `sDate` date DEFAULT NULL,
     *   `sDateTime` datetime DEFAULT NULL,
     *   `nDecimal` decimal(10,2) DEFAULT NULL,
     *   `sEnum` enum('active','inactive','pending') COLLATE utf8mb4_general_ci NOT NULL,
     *   `nFloat` float DEFAULT NULL,
     *   `nTinyint` tinyint DEFAULT NULL,
     *   `sTinyText` tinytext COLLATE utf8mb4_general_ci,
     *   `nYear` year DEFAULT NULL,
     *   `sVarBinary` varbinary(255) DEFAULT NULL,
     *   PRIMARY KEY (`id`)
     * ) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;
     */
    Timestamp nTimestamp;


//    @MiniEntityTableFieldName( name = "nTimestamp")
//    String time;
}
