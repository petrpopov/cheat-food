package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.entity.UserConnections;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 18:13
 */

@Component
public class UserConnectionsService extends GenericService<UserConnections> {

    public UserConnectionsService() {
        super(UserConnections.class);
        logger = Logger.getLogger(UserConnectionsService.class);
    }

    public UserConnections findByUser(String userId) {

        Criteria criteria = Criteria.where("user.$id").is(new ObjectId(userId));
        Query query = new Query(criteria);

        UserConnections user = op.findOne(query, domainClass);
        return user;
    }
}
