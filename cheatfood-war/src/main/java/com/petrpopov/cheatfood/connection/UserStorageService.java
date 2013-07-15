package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 12.02.13
 * Time: 13:33
 */
@Component
public class UserStorageService {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoOperations op;


    public UserEntity getUserByFoursquareId(String foursquareId)
    {
        Criteria criteria = Criteria.where("foursquareId").is(foursquareId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity getUserByCookieId(String cookie)
    {
        Criteria criteria = Criteria.where("cookieId").is(cookie);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity saveOrUpdate(UserEntity userEntity)
    {
        UserEntity u = this.getUserByFoursquareId(userEntity.getFoursquareId());
        if( u == null )
            op.save(userEntity);
        else
        {
            Update update = new Update()
                    .set("firstName", userEntity.getFirstName())
                    .set("lastName", userEntity.getLastName())
                    .set("foursquareToken", userEntity.getFoursquareToken() );

            Query query = new Query(Criteria.where("foursquareId").is(userEntity.getFoursquareId()));
            u = op.findAndModify(query, update,
                    new FindAndModifyOptions().returnNew(true), UserEntity.class);
        }

        return u;
    }

    public List<UserEntity> findAll() {
        return op.findAll(UserEntity.class);
    }
}
