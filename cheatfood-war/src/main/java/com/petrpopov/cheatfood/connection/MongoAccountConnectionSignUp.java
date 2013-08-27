package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.model.data.UserEntityInfo;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.UserRole;
import com.petrpopov.cheatfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.foursquare.api.ContactInfo;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.social.foursquare.api.FoursquareUser;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 15.02.13
 * Time: 12:57
 */

@Component
public class MongoAccountConnectionSignUp implements ConnectionSignUp {

    @Autowired
    private ConnectionAccessTokenFieldHandler connectionAccessTokenFieldHandler;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    public Boolean executeAndGetSavedOrUpdatedInfo(Connection<?> connection) {

        UserProfile profile = connection.fetchUserProfile();

        UserEntity userEntity = this.buildUserEntity(connection, profile);
        UserEntityInfo info = userService.saveOrUpdate(userEntity);

        return info.getSavedNew();
    }

    @Override
    public String execute(Connection<?> connection) {

        UserProfile profile = connection.fetchUserProfile();

        UserEntity userEntity = this.buildUserEntity(connection, profile);
        userService.saveOrUpdate(userEntity);

        return profile.getUsername();
    }

    private UserEntity buildUserEntity(Connection<?> connection, UserProfile profile) {

        String userId = connection.getKey().getProviderUserId();
        String token = connectionAccessTokenFieldHandler.getAccessTokenFromConnection(connection);

        Class<?> apiClass = providerIdClassStorage.getProviderClassByConnection(connection);

        UserEntity userEntity = new UserEntity();

        userEntity.setFirstName(profile.getFirstName());
        userEntity.setLastName(profile.getLastName());
        userEntity.setEmail( profile.getEmail() );

        if( apiClass.equals(Foursquare.class) ) {
            userEntity.setFoursquareId(userId);
            userEntity.setFoursquareToken(token);

            Foursquare api = (Foursquare) ((OAuth2Connection) connection).getApi();
            FoursquareUser foursquareUser = api.userOperations().getUser();

            ContactInfo contact = foursquareUser.getContact();
            if( contact != null ) {
                if( userEntity.getEmail() == null ) {
                    String email = contact.getEmail();
                    userEntity.setEmail(email);
                }

                String twitter = contact.getTwitter();
                if( twitter != null ) {
                    userEntity.setFoursquareTwitterUsername(twitter);
                }
            }
        }
        else if( apiClass.equals(Facebook.class) ) {
            userEntity.setFacebookId(userId);
            userEntity.setFacebookToken(token);
        }
        else if( apiClass.equals(Twitter.class) ) {
            userEntity.setTwitterId(userId);
            userEntity.setTwitterToken(token);
            userEntity.setTwitterUsername(profile.getUsername());
        }

        List<UserRole> roles = new ArrayList<UserRole>();
        roles.add(UserRole.getRoleUser());
        userEntity.setRoles(roles);

        return userEntity;
    }
}
