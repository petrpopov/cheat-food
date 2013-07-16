package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
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

    //@Cacheable("users")
    public UserEntity getUserByEmail(String email) {
        Criteria criteria = Criteria.where("email").is(email);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    @Cacheable(value = "users", key = "#id")
    public UserEntity getUserById(String id) {
        Criteria criteria = Criteria.where("_id").is(id);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

  //  @Cacheable("users")
    public UserEntity getUserByFoursquareId(String foursquareId)
    {
        Criteria criteria = Criteria.where("foursquareId").is(foursquareId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

   // @Cacheable("users")
    public UserEntity getUserByFacebookId(String facebookId) {
        Criteria criteria = Criteria.where("facebookId").is(facebookId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

  //  @Cacheable("users")
    public UserEntity getUserByCookieId(String cookie)
    {
        Criteria criteria = Criteria.where("cookieId").is(cookie);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    @CacheEvict(value = "users", key = "#userEntity.id")
    public UserEntity saveOrUpdate(UserEntity userEntity)
    {
        if(userEntity.getFoursquareId() != null) {
            return saveOrUpdateFoursquareUser(userEntity);
        }
        else if( userEntity.getFacebookId() != null ) {
            return saveOrUpdateFacebookUser(userEntity);
        }

        return userEntity;
    }

    private UserEntity saveOrUpdateFoursquareUser(UserEntity userEntity) {

        UserEntity u = this.getUserByFoursquareId(userEntity.getFoursquareId());

        if( u == null ) {

            if( userEntity.getEmail() != null ) {
                u = this.getUserByEmail(userEntity.getEmail());

                if( u == null )
                    return justSave(userEntity);
                else {
                    //found user with the same email. need to update only foursquare fields!
                    Update update = new Update()
                            .set("foursquareId", userEntity.getFoursquareId())
                            .set("foursquareToken", userEntity.getFoursquareToken() );

                    return findAndUpdate(userEntity, update, "email");
                }
            }
            else
                return justSave(userEntity); //did not found fs user and our email is null - cannot find this user in DB just save
        }
        else {
            //found saved foursquare user. need to update
            Update update = new Update()
                    .set("firstName", userEntity.getFirstName())
                    .set("lastName", userEntity.getLastName())
                    .set("foursquareToken", userEntity.getFoursquareToken() )
                    .set("email", userEntity.getEmail());

            return findAndUpdate(userEntity, update, "foursquareId");
        }
    }

    private UserEntity saveOrUpdateFacebookUser(UserEntity userEntity) {

        UserEntity u = this.getUserByFacebookId(userEntity.getFacebookId());

        if( u == null ) {
            if( userEntity.getEmail() != null ) {
                u = this.getUserByEmail(userEntity.getEmail());

                if( u == null )
                    return justSave(userEntity);
                else {
                    //found user with the same email. need to update only facebook fields!
                    Update update = new Update()
                            .set("facebookId", userEntity.getFacebookId())
                            .set("facebookToken", userEntity.getFacebookToken() );

                    return findAndUpdate(userEntity, update, "email");
                }
            }
            else
                return justSave(userEntity);
        }
        else {
            //found saved facebook user. need to update
            Update update = new Update()
                    .set("firstName", userEntity.getFirstName())
                    .set("lastName", userEntity.getLastName())
                    .set("facebookToken", userEntity.getFoursquareToken() )
                    .set("email", userEntity.getEmail());

            return findAndUpdate(userEntity, update, "facebookId");
        }
    }

    private UserEntity justSave(UserEntity userEntity) {
        op.save(userEntity);
        return userEntity;
    }

    private UserEntity findAndUpdate(UserEntity userEntity, Update update, String field) {

        BeanWrapper wrapper = new BeanWrapperImpl(userEntity);
        String value = (String) wrapper.getPropertyValue(field);

        Query query = new Query(Criteria.where(field).is(value));
        UserEntity u = op.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), UserEntity.class);
        return u;
    }

    public List<UserEntity> findAll() {
        return op.findAll(UserEntity.class);
    }
}
