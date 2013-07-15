package com.petrpopov.cheatfood.security;

import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 18.02.13
 * Time: 18:19
 */
@Component
public class UsernameAuthenticationTokenService {

    public UsernamePasswordAuthenticationToken getUsernamePasswordToken(RememberMeAuthenticationToken token)
    {
        UsernamePasswordAuthenticationToken token1 =
                new UsernamePasswordAuthenticationToken(token.getPrincipal(), token.getCredentials());

        return token1;
    }

}
