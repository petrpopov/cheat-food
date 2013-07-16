package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.foursquare.api.ContactInfo;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.social.foursquare.api.FoursquareUser;
import org.springframework.stereotype.Component;

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
    private UserStorageService userStorageService;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    @Override
    public String execute(Connection<?> connection) {

        UserProfile profile = connection.fetchUserProfile();

        UserEntity userEntity = this.buildUserEntity(connection, profile);
        userStorageService.saveOrUpdate(userEntity);

        return profile.getUsername();
    }

    private UserEntity buildUserEntity(Connection<?> connection, UserProfile profile) {

        String userId = connection.getKey().getProviderUserId();
        String token = connectionAccessTokenFieldHandler.getAccessTokenFromConnection((OAuth2Connection) connection);

        Class<?> apiClass = providerIdClassStorage.getProviderClassByConnection(connection);

        UserEntity userEntity = new UserEntity();
        userEntity.setFirstName(profile.getFirstName());
        userEntity.setLastName(profile.getLastName());
        userEntity.setEmail( profile.getEmail() );

        if( apiClass.equals(Foursquare.class) ) {
            userEntity.setFoursquareId(userId);
            userEntity.setFoursquareToken(token);

            if( userEntity.getEmail() == null ) {
                Foursquare api = (Foursquare) ((OAuth2Connection) connection).getApi();
                FoursquareUser foursquareUser = api.userOperations().getUser();

                ContactInfo contact = foursquareUser.getContact();
                if( contact != null ) {
                    String email = contact.getEmail();
                    userEntity.setEmail(email);
                }
            }
        }
        else if( apiClass.equals(Facebook.class) ) {
            userEntity.setFacebookId(userId);
            userEntity.setFacebookToken(token);
        }

        return userEntity;
    }
}
