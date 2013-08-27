package com.petrpopov.cheatfood.service;

import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
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

    protected Logger logger;

    public GenericService() {
        logger = Logger.getLogger(GenericService.class);
    }

    public GenericService(Class<T> domainClass) {
        this.domainClass = domainClass;
        logger = Logger.getLogger(domainClass);
    }

    public List<T> findAll() {
        logger.info("Returning all entities " + domainClass.getSimpleName() + " from database");
        return op.findAll(domainClass);
    }

    public T findById(String id) {
        logger.info("Returning entity " + domainClass.getSimpleName() + " from database by ID");
        return op.findById(id, domainClass);
    }

    public T save(T object) {
        op.save(object);
        return object;
    }

    protected void batchUpdateAllCollectionObjects() {

        String collectionName = op.getCollectionName(domainClass);
        DBCollection collection = op.getCollection(collectionName);
        DBCursor cursor = collection.find();

        while (cursor.hasNext()) {
            DBObject entity = cursor.next();

            if( !(entity instanceof BasicDBObject) ) {
                continue;
            }

            BasicDBObject dbo = (BasicDBObject) entity;
            updateEntityForBatchOperation(collection, dbo);
        }
    }

    protected void updateEntityForBatchOperation(DBCollection collection, BasicDBObject entity) {

    }
}
