package com.petrpopov.cheatfood.service;

import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.connection.ConnectionServiceFactory;
import com.petrpopov.cheatfood.model.data.*;
import com.petrpopov.cheatfood.model.entity.EmailChangeToken;
import com.petrpopov.cheatfood.model.entity.PasswordForgetToken;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.UserRole;
import com.petrpopov.cheatfood.security.CheatPasswordEncoder;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.MessagingException;
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

    @Autowired
    private PasswordForgetTokenService tokenService;

    @Autowired
    private EmailChangeTokenService emailChangeTokenService;

    @Autowired
    private ConnectionServiceFactory connectionServiceFactory;

    @Autowired
    private MailService mailService;

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
    public void removeUser(String userId) throws CheatException {

        if( userId == null )
            return;

        UserEntity entity = this.findById(userId);
        if( entity == null )
            return;

        op.remove(entity);
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

    @CacheEvict(value = "users", allEntries = true)
    @PreAuthorize("(hasRole('ROLE_USER') and #user.id==principal.username) or hasRole('ROLE_ADMIN')")
    public MessageResult updateUser(@Valid UserUpdate user, String globalUrl) throws CheatException, MessagingException {

        MessageResult res = new MessageResult();

        String id = user.getId();
        UserEntity byId = this.findById(id);

        if( byId == null )
            throw new CheatException(ErrorType.no_such_user);

        byId.setFirstName(user.getFirstName());
        byId.setLastName(user.getLastName());

        op.save(byId);

        //paranoia style
        String email = user.getEmail();
        if( email == null)
            return res;

        if( email.isEmpty() )
            return res;


        //if email from request is equal to current email
        if( byId.getEmail() != null && byId.getEmail().equals(user.getEmail()) ) {
            res.setResult(byId);
            return res;
        }

        UserEntity userByEmail = this.getUserByEmail(user.getEmail());
        if( userByEmail != null ) {
            if( !userByEmail.getId().equals(id))
                throw new CheatException(ErrorType.merge_users);
        }


        EmailChangeToken token = emailChangeTokenService.createTokenForEmail(user.getEmail(), user.getId());

        res.setWarning(true);
        res.setWarningType(WarningType.email_change);

        mailService.sendChangeEmailMail(user.getEmail(), token.getValue(), globalUrl);

        return res;
    }

    @CacheEvict(value = "users", allEntries = true)
    public void updateEmailForUser(UserEntity userEntity, String tokenid) throws CheatException {

        EmailChangeToken token = emailChangeTokenService.findByTokenValue(tokenid);
        if( token == null )
            throw new CheatException(ErrorType.wrong_token);

        if( token.getValid().equals(Boolean.FALSE) )
            throw new CheatException(ErrorType.token_invalid);

        String userId = token.getUserId();
        if( userId == null )
            throw new CheatException(ErrorType.no_such_user);

        if( !userId.equals(userEntity.getId()))
            throw new CheatException(ErrorType.access_denied);

        UserEntity user = this.findById(userId);
        user.setEmail(token.getEmail());
        op.save(user);

        emailChangeTokenService.invalidateTokensForEmail(token.getEmail());
    }

    @CacheEvict(value = "users", allEntries = true)
    public void updateEmailForUserFromCallback(String userId, String email) {

        if( email == null || userId == null )
            return;

        UserEntity entity = this.findById(userId);
        if( entity == null )
            return;

        entity.setEmail(email);
        op.save(entity);
    }

    public void forgetPasswordForUser(String email, String globalUrl) throws CheatException, MessagingException {

        if( email == null )
            throw new CheatException(ErrorType.email_is_empty);

        if( email.isEmpty() )
            throw new CheatException(ErrorType.email_is_empty);

        PasswordForgetToken tokenForEmail = tokenService.createTokenForEmail(email, null);

        mailService.sendForgetPasswordMail(email, tokenForEmail.getValue(), globalUrl);
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

    @Override
    protected void updateEntityForBatchOperation(DBCollection collection, BasicDBObject entity) {

        setDefaultRoles(collection, entity);
        removeConnectionsForEmptyEmail(entity);

        collection.save(entity);
    }

    private void setDefaultRoles(DBCollection collection, BasicDBObject entity) {

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

    private void removeConnectionsForEmptyEmail(BasicDBObject entity) {

        ObjectId _id = (ObjectId) entity.get("_id");

        Object email = entity.get("email");
        if( email != null )
            return;

        connectionServiceFactory.removeConnectionsForUser(_id.toString());
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
            //found saved twitter user. need to update
            Update update = new Update()
                    .set("twitterToken", userEntity.getTwitterToken() )
                    .set("twitterUsername", userEntity.getTwitterUsername() );

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
