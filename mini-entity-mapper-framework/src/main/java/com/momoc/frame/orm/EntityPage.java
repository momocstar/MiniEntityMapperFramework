package com.momoc.frame.orm;

import lombok.Data;

import java.util.List;

@Data
public class EntityPage<T> {

    Integer page;

    Integer pageSize;

    Long total;

    List<T> pageData;


    public EntityPage() {
        this.page = 1;
        this.pageSize = 10;
    }

    public EntityPage(Integer page, Integer pageSize) {
        this.page = page;
        this.pageSize = pageSize;
    }

}
