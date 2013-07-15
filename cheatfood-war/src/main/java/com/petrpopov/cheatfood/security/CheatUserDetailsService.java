package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.UserStorageService;
import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:33
 */
@Component
public class CheatUserDetailsService implements UserDetailsService {

    @Autowired
    private UserStorageService userStorageService;

    @Autowired
    private UserDetailsAssembler userDetailsAssembler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userStorageService.getUserByFoursquareId(username);

        if(user == null)
            throw new UsernameNotFoundException("User not found in MongoDB !");

        UserDetails u = userDetailsAssembler.fromUserToUserDetails(user);
        return u;
    }
}
