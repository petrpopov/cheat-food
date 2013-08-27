package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.model.data.AuthDetails;
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
    protected void additionalAuthenticationChecks(UserDetails userDetails, UsernamePasswordAuthenticationToken authentication)
            throws AuthenticationException {

        //do not check social accounts
        Class<?> apiClass = getApiClass(authentication);
        if( apiClass != null )
            return;


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

        boolean paswordUser;

        Class<?> apiClass = getApiClass(authentication);
        if( apiClass == null )
            paswordUser = true;
        else
            paswordUser = false;


        UserDetails u;
        try
        {
            if( paswordUser == false ) {
                u = cheatUserDetailsService.loadUserById(username, apiClass);
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

    private Class<?> getApiClass(UsernamePasswordAuthenticationToken authentication) throws AuthenticationException {

        Object details = authentication.getDetails();
        if( details == null ) {
            throw new BadCredentialsException("AuthDetails cannot be null !");
        }

        if( !(details instanceof AuthDetails) ) {
            throw new BadCredentialsException("AuthDetails must be a class of AuthDetails.class !");
        }

        Class<?> apiClass = ((AuthDetails)details).getApiClass();
        return apiClass;
    }
}
