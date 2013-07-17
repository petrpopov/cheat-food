package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.connection.ConnectionService;
import com.petrpopov.cheatfood.connection.ConnectionServiceFactory;
import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.security.CheatRememberMeServices;
import com.petrpopov.cheatfood.security.LoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.ConnectionSignUp;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.stereotype.Component;

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
    private ConnectionSignUp connectionSignUp;

    @Autowired
    private ConnectionRepository connectionRepository;

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    public void apiCallback(String code, Class<?> apiClass, HttpServletRequest request, HttpServletResponse response) {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        Connection<?> connection = connectionService.getConnection(code);

        //first - save user or update it
        if( connection instanceof OAuth2Connection<?> ) {
            OAuth2Connection<?> oAuth2Connection = (OAuth2Connection<?>) connection;
            connectionSignUp.execute(oAuth2Connection);
        }

        //second - use SpringSecurity auth services, because they use DB to retrieve user
        Authentication authentication = loginManager.authenticate(connection);
        //create cookies for remember-me shit
        rememberMeServices.onLoginSuccess(request, response, authentication);

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

    public String getAuthorizeUrl(String providerId) {

        ConnectionService<?> connectionService = registry.getConnectionService(providerId);
        return connectionService.getAuthorizeUrl(null);
    }

    public String getAuthorizeUrl(Class<?> apiClass) {

        return this.getAuthorizeUrl(apiClass, null);
    }

    public String getAuthorizeUrl(Class<?> apiClass, String scope) {

        ConnectionService<?> connectionService = registry.getConnectionService(apiClass);
        return connectionService.getAuthorizeUrl(scope);
    }
}
