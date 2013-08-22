package com.petrpopov.cheatfood.security;

import org.apache.log4j.Logger;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 22.08.13
 * Time: 19:36
 */

@Component
public class RememberMeProvider implements AuthenticationProvider {

    private Logger logger = Logger.getLogger(RememberMeProvider.class);
    protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {

        if (!supports(authentication.getClass())) {
            return null;
        }

        if (authentication.getCredentials() == null) {
            logger.debug("Authentication failed: no credentials provided");

            throw new BadCredentialsException(messages.getMessage(
                    "AbstractUserDetailsAuthenticationProvider.badCredentials", "Bad credentials"));
        }

        /*if (this.key.hashCode() != ((RememberMeAuthenticationToken) authentication).getKeyHash()) {
            throw new BadCredentialsException(messages.getMessage("RememberMeAuthenticationProvider.incorrectKey",
                    "The presented RememberMeAuthenticationToken does not contain the expected key"));
        } */

        return authentication;
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (RememberMeAuthenticationToken.class.isAssignableFrom(authentication));
    }
}
