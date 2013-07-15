package com.petrpopov.cheatfood.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 15:14
 */
@Component
public class GenericConnectionService<T> implements ConnectionService {

    private Class domainClass;
    private String callbackUrl;

    @Autowired
    private ConnectionFactoryLocator registry;

    public GenericConnectionService() {
    }

    public GenericConnectionService(ConnectionFactoryLocator registry, String callbackUrl, Class domainClass) {
        this.registry = registry;
        this.domainClass = domainClass;
        this.callbackUrl = callbackUrl;
    }

    public GenericConnectionService(String callbackUrl, Class domainClass) {
        this.domainClass = domainClass;
        this.callbackUrl = callbackUrl;
    }

    @Override
    public String getProviderId() {
        OAuth2ConnectionFactory<T> connectionFactory = getConnectionFactory();
        return connectionFactory.getProviderId();
    }

    @Override
    public String getAuthorizeUrl() {
        OAuth2ConnectionFactory<T> connectionFactory = getConnectionFactory();

        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        OAuth2Parameters params = new OAuth2Parameters();
        params.setRedirectUri( callbackUrl );

        String url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
        return url;
    }

    @Override
    public Connection<T> getConnection(String code) {
        OAuth2ConnectionFactory<T> connectionFactory = getConnectionFactory();

        OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
        AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, callbackUrl, null);
        Connection<T> connection = connectionFactory.createConnection(accessGrant);

        return connection;
    }

    private OAuth2ConnectionFactory<T> getConnectionFactory() {
        OAuth2ConnectionFactory<T> connectionFactory = (OAuth2ConnectionFactory<T>) registry.getConnectionFactory(domainClass);
        return connectionFactory;
    }
}
