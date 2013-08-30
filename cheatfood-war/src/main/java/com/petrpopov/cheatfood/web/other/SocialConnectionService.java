package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.connection.*;
import com.petrpopov.cheatfood.security.CheatRememberMeServices;
import com.petrpopov.cheatfood.security.LoginManager;
import com.petrpopov.cheatfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

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
    private UserService userService;

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

    public UserEmailInfo apiCallbackOAuth1(String oauth_verifier, Class<?> apiClass, NativeWebRequest request, HttpServletResponse response) {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        Connection<?> connection = connectionService.getConnectionOAuth1(oauth_verifier, request);
        if(connection == null) {
            return null;
        }

        UserEmailInfo info = developConnection(connection);
        if( info.getEmail() == null ) {
            request.setAttribute("apiConnection", connection, RequestAttributes.SCOPE_SESSION);
            request.setAttribute("apiClass", apiClass, RequestAttributes.SCOPE_SESSION);
            return info;
        }


        return authorize(info, connection, apiClass, request, response);
    }

    public UserEmailInfo apiCallbackOAuth2(String code, Class<?> apiClass, NativeWebRequest request, HttpServletResponse response)
    {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        Connection<?> connection = connectionService.getConnectionOAuth2(code);

        UserEmailInfo info = developConnection(connection);
        if( info.getEmail() == null ) {
            request.setAttribute("apiConnection", connection, RequestAttributes.SCOPE_SESSION);
            request.setAttribute("apiClass", apiClass, RequestAttributes.SCOPE_SESSION);
            return info;
        }


        return authorize(info, connection, apiClass, request, response);
    }

    private UserEmailInfo developConnection(Connection<?> connection) {
        //first - save user or update it
        UserEmailInfo userInfo = connectionSignUp.executeAndGetSavedOrUpdatedInfo(connection);
        return userInfo;
    }

    public UserEmailInfo authorize(UserEmailInfo userInfo,
                           Connection<?> connection, Class<?> apiClass, NativeWebRequest request, HttpServletResponse response) {

        userService.updateEmailForUserFromCallback(userInfo.getUserId(), userInfo.getEmail());

        //second - use SpringSecurity auth services, because they use DB to retrieve user
        Authentication authentication = loginManager.authenticate(connection, userInfo.getNewUser());

        //create cookies for remember-me shit
        rememberMeServices.onLoginSuccess((HttpServletRequest) request.getNativeRequest(), response, authentication);

        //then play with connections
        try {
            if( connectionRepository.findConnections(apiClass).size() > 0 )
                connectionRepository.removeConnections(providerIdClassStorage.getProviderIdByClass(apiClass));
        }
        catch (Exception e) {}

        connectionRepository.addConnection(connection);

        return userInfo;
    }

    public void deAuthorize(String userId, NativeWebRequest request, HttpServletResponse response) {

        try {
            userService.removeUser(userId);
        } catch (CheatException e) {
            e.printStackTrace();
        }

        loginManager.logout((HttpServletRequest) request.getNativeRequest(), response);
        registry.removeConnectionsForUser(userId);
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
