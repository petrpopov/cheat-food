package com.petrpopov.cheatfood.service;

import org.apache.log4j.Logger;
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
public class GenericService<T> {

    @Autowired
    @Qualifier("mongoTemplate")
    protected MongoOperations op;

    protected Class<T> domainClass;

    private Logger logger;

    public GenericService() {
        logger = Logger.getLogger(GenericService.class);
    }

    public GenericService(Class<T> domainClass) {
        this.domainClass = domainClass;
        logger = Logger.getLogger(domainClass);
    }

    public List findAll() {
        logger.info("Returning all entities " + domainClass.getSimpleName() + " from database");
        return op.findAll(domainClass);
    }

    public T findById(String id) {
        logger.info("Returning entity " + domainClass.getSimpleName() + " from database by ID");
        return op.findById(id, domainClass);
    }
}
