package com.petrpopov.cheatfood.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactory;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.support.OAuth1ConnectionFactory;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;
import org.springframework.social.oauth1.AuthorizedRequestToken;
import org.springframework.social.oauth1.OAuth1Operations;
import org.springframework.social.oauth1.OAuth1Parameters;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.WebRequest;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 15:14
 */
@Component
public class GenericConnectionService<T> implements ConnectionService {

    private Class domainClass;
    private String callbackUrl;

    public static final String OAUTH_TOKEN_ATTRIBUTE = "oauthToken";

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
        ConnectionFactory<T> connectionFactory = getConnectionFactory();
        return connectionFactory.getProviderId();
    }

    @Override
    public String getAuthorizeUrl(String scope, NativeWebRequest request) {

        ConnectionFactory<T> connectionFactory = getConnectionFactory();
        if( connectionFactory instanceof OAuth2ConnectionFactory) {

            OAuth2ConnectionFactory oAuth2ConnectionFactory = (OAuth2ConnectionFactory) connectionFactory;

            OAuth2Operations oauthOperations = oAuth2ConnectionFactory.getOAuthOperations();
            OAuth2Parameters params = getOAuth2Parameters(scope);

            String url = oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE, params);
            return url;
        }
        else if( connectionFactory instanceof OAuth1ConnectionFactory) {

            OAuth1ConnectionFactory oAuth1ConnectionFactory = (OAuth1ConnectionFactory) connectionFactory;
            OAuth1Operations oAuthOperations = oAuth1ConnectionFactory.getOAuthOperations();
            OAuth1Parameters params = getOAuth1Parameters();

            OAuthToken requestToken = oAuthOperations.fetchRequestToken(callbackUrl, null);
            request.setAttribute(OAUTH_TOKEN_ATTRIBUTE, requestToken, RequestAttributes.SCOPE_SESSION);

            String url = oAuthOperations.buildAuthorizeUrl(requestToken.getValue(), params);
            return url;
        }

        return null;
    }

    @Override
    public Connection getConnectionOAuth1(String oauth_verifier, NativeWebRequest request) {

        ConnectionFactory<T> connectionFactory = getConnectionFactory();

        if( connectionFactory instanceof OAuth1ConnectionFactory) {

            OAuth1ConnectionFactory oAuth1ConnectionFactory = (OAuth1ConnectionFactory) connectionFactory;
            OAuth1Operations oAuthOperations = oAuth1ConnectionFactory.getOAuthOperations();

            OAuthToken tokenValue = extractCachedRequestToken(request);
            if(tokenValue == null)
                return null;

            AuthorizedRequestToken requestToken = new AuthorizedRequestToken(tokenValue, oauth_verifier);


            OAuthToken accessToken = oAuthOperations.exchangeForAccessToken(requestToken, null);
            return oAuth1ConnectionFactory.createConnection(accessToken);
        }

        return null;
    }

    @Override
    public Connection getConnectionOAuth2(String code) {

        ConnectionFactory<T> connectionFactory = getConnectionFactory();

        if( connectionFactory instanceof OAuth2ConnectionFactory) {

            OAuth2ConnectionFactory oAuth2ConnectionFactory = (OAuth2ConnectionFactory) connectionFactory;

            OAuth2Operations oauthOperations = oAuth2ConnectionFactory.getOAuthOperations();
            AccessGrant accessGrant = oauthOperations.exchangeForAccess(code, callbackUrl, null);
            Connection<T> connection = oAuth2ConnectionFactory.createConnection(accessGrant);

            return connection;
        }

        return null;
    }

    private OAuthToken extractCachedRequestToken(WebRequest request) {
        OAuthToken requestToken = (OAuthToken) request.getAttribute(OAUTH_TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
        //request.removeAttribute(OAUTH_TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
        return requestToken;
    }


    private ConnectionFactory<T> getConnectionFactory() {

        ConnectionFactory factory = registry.getConnectionFactory(domainClass);
        return factory;
    }

    private OAuth1Parameters getOAuth1Parameters() {

        OAuth1Parameters params = new OAuth1Parameters();
        params.setCallbackUrl(callbackUrl);
        return params;
    }

    private OAuth2Parameters getOAuth2Parameters(String scope) {
        OAuth2Parameters params = new OAuth2Parameters();

        if( scope != null ) {
            params.setScope(scope);
        }
        params.setRedirectUri( callbackUrl );
        return params;
    }
}
