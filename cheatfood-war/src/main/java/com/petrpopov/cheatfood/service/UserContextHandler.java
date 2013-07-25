package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 16.07.13
 * Time: 21:12
 */

@Component
public class UserContextHandler {

    @Autowired
    private IUserService userService;

    public UserEntity currentContextUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if( authentication == null ) {
            return null;
        }

        if( authentication instanceof UsernamePasswordAuthenticationToken) {

            String username = (authentication.getPrincipal() == null) ? null : authentication.getName();
            if( username == null )
                return null;

            UserEntity entity = userService.getUserById(username);

            return entity;
        }

        return null;
    }

    public boolean isPasswordUser(Authentication authentication) {

        boolean paswordUser;

        Object details = authentication.getDetails();

        if( details instanceof Class<?> ) {
            paswordUser = false;
        }
        else {
            paswordUser = true;
        }

        return paswordUser;
    }

    public Class<?> getUserSocialNetworkClass(Authentication authentication) {

        Object details = authentication.getDetails();
        if( details instanceof Class<?> ) {
            return (Class<?>) details;
        }

        return null;
    }
}
