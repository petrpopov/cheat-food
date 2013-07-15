package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.UserProfile;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 15.02.13
 * Time: 12:57
 */

@Component
public class MongoAccountConnectionSignUp implements ConnectionSignUp {

    @Autowired
    private FoursquareConnectionFieldHandler foursquareConnectionFieldHandler;

    @Autowired
    private UserStorageService userStorageService;

    @Override
    public String execute(Connection<?> connection) {

        UserProfile profile = connection.fetchUserProfile();

        UserEntity userEntity = new UserEntity();
        userEntity.setFoursquareId(connection.getKey().getProviderUserId());
        userEntity.setFirstName(profile.getFirstName());
        userEntity.setLastName( profile.getLastName() );
        userEntity.setFoursquareToken(foursquareConnectionFieldHandler.getAccessTokenFromConnection((OAuth2Connection) connection));

        userStorageService.saveOrUpdate(userEntity);

        return profile.getUsername();
    }
}
