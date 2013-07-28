package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.impl.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:33
 */
@Component
public class CheatUserDetailsService implements UserDetailsService {

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsAssembler userDetailsAssembler;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserEntity user = userService.getUserById(username);

        return convertUser(user);
    }

    public UserDetails loadUserById(String id, Class<?> apiClass) {

        UserEntity user = null;

        if( apiClass.equals(Foursquare.class) ) {
            user = loadUserByFoursquareId(id);
        }
        else if( apiClass.equals(Facebook.class) ) {
            user = loadUserByFacebookId(id);
        }
        else if( apiClass.equals(Twitter.class) ) {
            user = loadUserByTwitterId(id);
        }
        else {
            return loadUserByUsername(id);
        }

        return convertUser(user, apiClass);
    }

    private UserEntity loadUserByFoursquareId(String foursquareId) {
        UserEntity user = userService.getUserByFoursquareId(foursquareId);
        return user;
    }

    private UserEntity loadUserByFacebookId(String facebookId) {
        UserEntity user = userService.getUserByFacebookId(facebookId);
        return user;
    }

    private UserEntity loadUserByTwitterId(String twitterId) {
        UserEntity user = userService.getUserByTwitterId(twitterId);
        return user;
    }

    private UserDetails convertUser(UserEntity user) throws UsernameNotFoundException {
        if(user == null)
            throw new UsernameNotFoundException("User not found in MongoDB !");

        UserDetails u = userDetailsAssembler.fromUserToUserDetails(user);
        return u;
    }

    private UserDetails convertUser(UserEntity user, Class<?> apiClass) throws UsernameNotFoundException {
        if(user == null)
            throw new UsernameNotFoundException("User not found in database !");

        UserDetails u = userDetailsAssembler.fromUserToUserDetails(user, apiClass);
        return u;
    }
}
