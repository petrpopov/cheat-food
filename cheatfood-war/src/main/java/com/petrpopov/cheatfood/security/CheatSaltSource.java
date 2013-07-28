package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.dao.SaltSource;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 16.07.13
 * Time: 16:53
 */

@Component
public class CheatSaltSource implements SaltSource {

    @Autowired
    private UserService userService;

    @Override
    public Object getSalt(UserDetails user) {

        String username = user.getUsername();

        UserEntity entity = userService.getUserById(username);
        if(entity == null) {
            return null;
        }

        return entity.getSalt();
    }
}
