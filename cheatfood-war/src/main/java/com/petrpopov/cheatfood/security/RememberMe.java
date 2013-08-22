package com.petrpopov.cheatfood.security;

import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * User: petrpopov
 * Date: 17.07.13
 * Time: 18:21
 */
public class RememberMe {

    private Authentication authentication;
    private AbstractAuthenticationToken token;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public AbstractAuthenticationToken getToken() {
        return token;
    }

    public void setToken(AbstractAuthenticationToken token) {
        this.token = token;
    }
}
