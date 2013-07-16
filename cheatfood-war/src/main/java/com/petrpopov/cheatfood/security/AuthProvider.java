package com.petrpopov.cheatfood.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.AbstractUserDetailsAuthenticationProvider;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.authentication.encoding.PasswordEncoder;
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

    @Autowired
    private SaltSource saltSource;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @Override
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        Object details = authentication.getDetails();
        if( details instanceof Class<?> ) {
            //do not check salt for social accounts
            return;
        }

        Object salt = null;
        if (this.saltSource != null) {
            salt = this.saltSource.getSalt(userDetails);

            if( salt == null )
                return;
        }

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }

        String presentedPassword = authentication.getCredentials().toString();

        if (!passwordEncoder.isPasswordValid(userDetails.getPassword(), presentedPassword, salt)) {
            logger.debug("Authentication failed: password does not match stored value");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"), userDetails);
        }
    }

    @Override
    protected UserDetails retrieveUser(String username, UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        boolean paswordUser = false;
        Object details = authentication.getDetails();
        Class<?> clazz = null;

        if( details instanceof Class<?> ) {
            paswordUser = false;
            clazz = (Class<?>) details;
        }
        else {
            paswordUser = true;
        }

        UserDetails u = null;
        try
        {
            if( paswordUser == false ) {
                u = cheatUserDetailsService.loadUserById(username, clazz);
            }
            else {
                u = cheatUserDetailsService.loadUserByUsername(username);
            }
        }
        catch (UsernameNotFoundException e)
        {
            throw e;
        }

        if( u == null )
            throw new AuthenticationServiceException("User cannot be null !");

        if( paswordUser == false ) {
            userDetailsFieldHandler.setPassword(u, authentication);
        }

        return u;
    }

    @Override
    public boolean supports(Class<?> authentication) {

        if( authentication.equals(RememberMeAuthenticationToken.class) )
            return true;

        return super.supports(authentication);
    }

}
