package com.momoc.frame.orm.poll;

import lombok.Data;

@Data
public class DBParams {

    public DBParams(String key, Object value) {
        this.key = key;
        this.value = value;
    }

    String key;
    Object value;
}
