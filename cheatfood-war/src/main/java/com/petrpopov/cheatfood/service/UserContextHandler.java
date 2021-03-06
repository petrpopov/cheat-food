package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.data.AuthDetails;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
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
    private UserService userService;

    @Value("#{properties.admin_username}")
    private String adminUsername;

    public UserEntity currentContextUser() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if( authentication == null ) {
            return null;
        }

        if( authentication instanceof UsernamePasswordAuthenticationToken
                || authentication instanceof RememberMeAuthenticationToken) {

            String username = (authentication.getPrincipal() == null) ? null : authentication.getName();
            if( username == null )
                return null;

            if( username.equals(adminUsername)) {
                UserEntity adminUserEntity = userService.getAdminUserEntity();
                return adminUserEntity;
            }

            UserEntity entity = userService.getUserById(username);

            return entity;
        }

        return null;
    }

    public AuthDetails currentAuthDetails() {

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if( authentication == null ) {
            return null;
        }

        Object details = authentication.getDetails();
        if( details instanceof AuthDetails )
            return (AuthDetails) details;

        return null;
    }
}
