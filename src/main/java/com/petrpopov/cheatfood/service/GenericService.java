package com.petrpopov.cheatfood.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 12.07.13
 * Time: 12:51
 */

@Component
public class GenericService<T> implements IGenericService<T> {

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoOperations op;

    protected Class<T> domainClass;

    public GenericService() {
    }

    public GenericService(Class<T> domainClass) {
        this.domainClass = domainClass;
    }

    @Override
    public List findAll() {
        return op.findAll(domainClass);
    }

    @Override
    public T findById(String id) {
        return op.findById(id, domainClass);
    }
}
