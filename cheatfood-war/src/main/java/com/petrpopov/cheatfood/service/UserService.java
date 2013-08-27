package com.petrpopov.cheatfood.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.UserCreate;
import com.petrpopov.cheatfood.model.data.UserEntityInfo;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.UserRole;
import com.petrpopov.cheatfood.security.CheatPasswordEncoder;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.FindAndModifyOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * User: petrpopov
 * Date: 12.02.13
 * Time: 13:33
 */
@Component
public class UserService extends GenericService<UserEntity> {

    @Autowired
    @Qualifier("mongoTemplate")
    private MongoOperations op;

    @Value("#{properties.admin_username}")
    private String adminUsername;

    @Autowired
    private CheatPasswordEncoder encoder;

    public UserService() {
        super(UserEntity.class);
        logger = Logger.getLogger(UserService.class);
    }

    @PostConstruct
    public void init() {

        if( !op.collectionExists(UserEntity.class) )
            return;

        logger.info("Checking all users for having default " + UserRole.ROLE_USER + " role" );

        batchUpdateAllCollectionObjects();
    }

    @CacheEvict(value = "users", allEntries = true)
    public UserEntity createUser(@Valid UserCreate user) throws CheatException {

        String email = user.getEmail();
        if( email.equals(adminUsername) ) {
            throw new CheatException(ErrorType.user_already_exists);
        }

        UserEntity userToSave = new UserEntity();
        UserEntity userByEmail = this.getUserByEmail(email);

        if( userByEmail != null ) {
            String passwordHash = userByEmail.getPasswordHash();

            if(passwordHash != null ) {
                //user already registered with password-email
                throw new CheatException(ErrorType.user_already_exists);
            }

            userToSave = userByEmail;
        }

        List<UserRole> roles = userToSave.getRoles();
        if( roles == null ) {
            roles = new ArrayList<UserRole>();
            roles.add(UserRole.getRoleUser());

            userToSave.setRoles(roles);
        }

        userToSave.setEmail(email);
        return updatePasswordForUser(userToSave, user.getPassword());
    }

    public UserEntity updatePasswordForUser(UserEntity userToSave, String password) {

        //generate random salt
        UUID randomUUID = UUID.randomUUID();
        String newSalt = randomUUID.toString();


        String encodePassword = encoder.encodePassword(password, newSalt);
        userToSave.setPasswordHash(encodePassword);
        userToSave.setSalt(newSalt);

        op.save(userToSave);
        return getUserByEmail(userToSave.getEmail());
    }

    //@Cacheable("users")
    public UserEntity getUserByEmail(String email) {
        Criteria criteria = Criteria.where("email").is(email);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity getAdminUserEntity() {

        UserEntity entity = new UserEntity();
        entity.setId(adminUsername);
        entity.setEmail(adminUsername);

        return entity;
    }

    @Cacheable(value = "users", key = "#id")
    public UserEntity getUserById(String id) {
        return this.findById(id);
    }

    public boolean isUserAdmin(String id) {

        UserEntity userById = this.getUserById(id);
        return isUserAdmin(userById);
    }

    public boolean isUserAdmin(UserEntity entity) {

        if( entity == null )
            return false;

        List<UserRole> roles = entity.getRoles();
        if( roles == null )
            return false;

        for (UserRole role : roles) {
            if( role.getName().equals(UserRole.ROLE_ADMIN) )
                return true;
        }

        return false;
    }

  //  @Cacheable("users")
    public UserEntity getUserByFoursquareId(String foursquareId)
    {
        Criteria criteria = Criteria.where("foursquareId").is(foursquareId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity getUserByFoursquareTwitterUsername(String foursquareTwitterUsername) {
        Criteria criteria = Criteria.where("foursquareTwitterUsername").is(foursquareTwitterUsername);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    // @Cacheable("users")
    public UserEntity getUserByFacebookId(String facebookId) {
        Criteria criteria = Criteria.where("facebookId").is(facebookId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity getUserByTwitterId(String twitterId) {
        Criteria criteria = Criteria.where("twitterId").is(twitterId);
        Query query = new Query(criteria);

        return op.findOne(query, UserEntity.class);
    }

    public UserEntity getUserByTwitterUsername(String twitterUsername) {
        Criteria criteria = Criteria.where("twitterUsername").is(twitterUsername);
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

    @CacheEvict(value = "users", allEntries = true) //key = "#userEntity.id")
    public UserEntityInfo saveOrUpdate(UserEntity userEntity)
    {
        if(userEntity.getFoursquareId() != null) {
            return saveOrUpdateFoursquareUser(userEntity);
        }
        else if( userEntity.getFacebookId() != null ) {
            return saveOrUpdateFacebookUser(userEntity);
        }
        else if( userEntity.getTwitterId() != null ) {
            return saveOrUpdateTwitterUser(userEntity);
        }

        return new UserEntityInfo(userEntity);
    }

    @Override
    protected void updateEntityForBatchOperation(DBCollection collection, BasicDBObject entity) {
        Object roles = entity.get("roles");

        if( roles != null ) {
            return;
        }

        logger.info("Updating user " + entity.get("_id") + " with default roles");

        //need to insert default roles
        BasicDBList rolesList = new BasicDBList();
        rolesList.put(0, UserRole.getRoleUser().getBasicDBObject() );
        entity.put("roles", rolesList);

        collection.save(entity);
    }

    private UserEntityInfo saveOrUpdateFoursquareUser(UserEntity userEntity) {

        UserEntity u = this.getUserByFoursquareId(userEntity.getFoursquareId());

        if( u == null ) {

            if( userEntity.getEmail() != null ) {
                u = this.getUserByEmail(userEntity.getEmail());

                if( u == null )
                    return saveOrUpdateByFoursquareTwitterUsername(userEntity);
                else {
                    //found user with the same email. need to update only foursquare fields!
                    Update update = new Update()
                            .set("foursquareId", userEntity.getFoursquareId())
                            .set("foursquareToken", userEntity.getFoursquareToken() )
                            .set("foursquareTwitterUsername", userEntity.getFoursquareTwitterUsername());

                    return findAndUpdate(userEntity, update, "email");
                }
            }
            else
                return saveOrUpdateByFoursquareTwitterUsername(userEntity);
        }
        else {
            //found saved foursquare user. need to update
            Update update = new Update()
                    .set("firstName", userEntity.getFirstName())
                    .set("lastName", userEntity.getLastName())
                    .set("foursquareToken", userEntity.getFoursquareToken() )
                    .set("foursquareTwitterUsername", userEntity.getFoursquareTwitterUsername())
                    .set("email", userEntity.getEmail());

            return findAndUpdate(userEntity, update, "foursquareId");
        }
    }

    private UserEntityInfo saveOrUpdateByFoursquareTwitterUsername(UserEntity userEntity) {
        String foursquareTwitterUsername = userEntity.getFoursquareTwitterUsername();

        if( foursquareTwitterUsername != null ) {
            UserEntity userByTwitterUsername = this.getUserByTwitterUsername(foursquareTwitterUsername);

            if( userByTwitterUsername == null )
                return justSave(userEntity); //did not found fs user and our email is null - cannot find this user in DB just save
            else {
                Update update = new Update()
                        .set("foursquareId", userEntity.getFoursquareId())
                        .set("foursquareToken", userEntity.getFoursquareToken() )
                        .set("foursquareTwitterUsername", userEntity.getFoursquareTwitterUsername())
                        .set("email", userEntity.getEmail());

                return findAndUpdate(userByTwitterUsername, update, "twitterUsername");
            }
        }
        else {
            return justSave(userEntity); //did not found fs user and our email is null - cannot find this user in DB just save
        }
    }

    private UserEntityInfo saveOrUpdateFacebookUser(UserEntity userEntity) {

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
                    .set("facebookToken", userEntity.getFacebookToken() )
                    .set("email", userEntity.getEmail());

            return findAndUpdate(userEntity, update, "facebookId");
        }
    }

    private UserEntityInfo saveOrUpdateTwitterUser(UserEntity userEntity) {

        UserEntity u = this.getUserByTwitterId(userEntity.getTwitterId());

        if( u == null ) {
            if( userEntity.getEmail() != null ) {
                u = this.getUserByEmail(userEntity.getEmail());

                if( u == null )
                    return saveOrUpdateByTwitterUsername(userEntity);
                else {
                    //found user with the same email. need to update only twitter fields!
                    Update update = new Update()
                            .set("twitterId", userEntity.getTwitterId())
                            .set("twitterToken", userEntity.getTwitterToken() )
                            .set("twitterUsername", userEntity.getTwitterUsername() );

                    return findAndUpdate(userEntity, update, "email");
                }
            }
            else
                return saveOrUpdateByTwitterUsername(userEntity);
        }
        else {
            //found saved facebook user. need to update
            Update update = new Update()
                    .set("firstName", userEntity.getFirstName())
                    .set("lastName", userEntity.getLastName())
                    .set("twitterToken", userEntity.getTwitterToken() )
                    .set("twitterUsername", userEntity.getTwitterUsername() )
                    .set("email", userEntity.getEmail());

            return findAndUpdate(userEntity, update, "twitterId");
        }
    }

    private UserEntityInfo saveOrUpdateByTwitterUsername(UserEntity userEntity) {
        String twitterUsername = userEntity.getTwitterUsername();

        if( twitterUsername != null ) {
            UserEntity entity = this.getUserByFoursquareTwitterUsername(twitterUsername);

            if( entity == null )
                return justSave(userEntity);
            else {
                Update update = new Update()
                        .set("twitterId", userEntity.getTwitterId())
                        .set("twitterToken", userEntity.getTwitterToken() )
                        .set("twitterUsername", userEntity.getTwitterUsername() );

                return findAndUpdate(entity, update, "foursquareTwitterUsername");
            }
        }
        else {
            return justSave(userEntity);
        }
    }

    private UserEntityInfo justSave(UserEntity userEntity) {
        op.save(userEntity);
        return new UserEntityInfo(userEntity);
    }

    private UserEntityInfo findAndUpdate(UserEntity userEntity, Update update, String field) {

        BeanWrapper wrapper = new BeanWrapperImpl(userEntity);
        String value = (String) wrapper.getPropertyValue(field);

        Query query = new Query(Criteria.where(field).is(value));
        UserEntity u = op.findAndModify(query, update, new FindAndModifyOptions().returnNew(true), UserEntity.class);
        return new UserEntityInfo(u, false);
    }
}
