package com.petrpopov.cheatfood.connection;

import org.springframework.social.connect.Connection;

/**
 * User: petrpopov
 * Date: 25.02.13
 * Time: 17:46
 */
public interface ConnectionService<T> {

    public String getProviderId();
    public String getAuthorizeUrl();
    public Connection<T> getConnection(String code);
}
