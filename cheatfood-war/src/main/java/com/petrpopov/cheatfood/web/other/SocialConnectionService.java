package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.connection.ConnectionService;
import com.petrpopov.cheatfood.connection.ConnectionServiceFactory;
import com.petrpopov.cheatfood.connection.MongoAccountConnectionSignUp;
import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.security.CheatRememberMeServices;
import com.petrpopov.cheatfood.security.LoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 15:29
 */

@Component
public class SocialConnectionService {

    @Autowired
    private ConnectionServiceFactory registry;

    @Autowired
    private MongoAccountConnectionSignUp connectionSignUp;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    public void apiCallback(String code, String oauth_verifier, Class<?> apiClass, NativeWebRequest request, HttpServletResponse response) {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        Connection<?> connection = connectionService.getConnection(code, oauth_verifier, request);

        //first - save user or update it
        Boolean newUser = connectionSignUp.executeAndGetSavedOrUpdatedInfo(connection);

        //second - use SpringSecurity auth services, because they use DB to retrieve user
        Authentication authentication = loginManager.authenticate(connection, newUser);
        //create cookies for remember-me shit
        rememberMeServices.onLoginSuccess((HttpServletRequest) request.getNativeRequest(), response, authentication);

        //then play with connections
        try {
            if( connectionRepository.findConnections(apiClass).size() > 0 )
                connectionRepository.removeConnections(providerIdClassStorage.getProviderIdByClass(apiClass));
        }
        catch (Exception e) {}

        connectionRepository.addConnection(connection);
    }

    public void logout(HttpServletRequest request, HttpServletResponse response) {
        // connectionRepository.removeConnections("foursquare");

        loginManager.logout(request, response);
    }

    public String getAuthorizeUrl(String providerId, NativeWebRequest request) {

        ConnectionService<?> connectionService = registry.getConnectionService(providerId);
        return connectionService.getAuthorizeUrl(null, request);
    }

    public String getAuthorizeUrl(Class<?> apiClass, NativeWebRequest request) {

        return this.getAuthorizeUrl(apiClass, null, request);
    }

    public String getAuthorizeUrl(Class<?> apiClass, String scope, NativeWebRequest request) {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        return connectionService.getAuthorizeUrl(scope, request);
    }
}
