package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:36
 */
@Component
public class UserDetailsAssembler {

    public UserDetails fromUserToUserDetails(UserEntity user)
    {
        String username = user.getFoursquareId();
        String password = "";

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority( "ROLE_USER") );
        authorities.add(new SimpleGrantedAuthority( "ROLE_FOURSQUARE") );

        User userDetails = new User(username, password, enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return userDetails;
    }
}
