package com.petrpopov.cheatfood.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:30
 */
@Component
public class AuthProvider extends AbstractUserDetailsAuthenticationProvider
{
    @Autowired
    private CheatUserDetailsService cheatUserDetailsService;

    @Autowired
    private UserDetailsFieldHandler userDetailsFieldHandler;

    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        UserDetails u;
        try
        {
            u = cheatUserDetailsService.loadUserByUsername(username);
            userDetailsFieldHandler.setPassword(u, authentication);
        }
        catch (UsernameNotFoundException e)
        {
            throw e;
        }

        if( u == null )
            throw new AuthenticationServiceException("User cannot be null !");

        return u;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        if( authentication.equals(RememberMeAuthenticationToken.class) )
            return true;

        return super.supports(authentication);
    }
}
