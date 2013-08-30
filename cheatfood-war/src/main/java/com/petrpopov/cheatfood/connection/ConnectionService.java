package com.petrpopov.cheatfood.connection;

import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.NativeWebRequest;

/**
 * User: petrpopov
 * Date: 25.02.13
 * Time: 17:46
 */
public interface ConnectionService<T> {

    public String getProviderId();
    public String getAuthorizeUrl(String scope, NativeWebRequest request);
    public Connection<T> getConnectionOAuth1(String oauth_verifier, NativeWebRequest request);
    public Connection<T> getConnectionOAuth2(String code);
}
