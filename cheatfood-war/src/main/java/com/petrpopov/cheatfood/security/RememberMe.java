package com.petrpopov.cheatfood.security;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;

/**
 * User: petrpopov
 * Date: 17.07.13
 * Time: 18:21
 */
public class RememberMe {

    private Authentication authentication;
    private UsernamePasswordAuthenticationToken token;

    public Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(Authentication authentication) {
        this.authentication = authentication;
    }

    public UsernamePasswordAuthenticationToken getToken() {
        return token;
    }

    public void setToken(UsernamePasswordAuthenticationToken token) {
        this.token = token;
    }
}
