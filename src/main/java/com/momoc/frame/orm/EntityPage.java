package com.momoc.frame.orm;

import lombok.Data;

import java.util.List;

@Data
public class EntityPage<T> {

    Integer page;

    Integer pageSize;

    Long total;

    List<T> pageData;
}
