package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.model.UserRole;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:36
 */
@Component
public class UserDetailsAssembler {


    public UserDetails fromUserToUserDetails(UserEntity user)
    {
        String username = this.getUsername(user, null);
        String password = this.getPassword(user, null);
        return buildUser(user, username, password);
    }

    public UserDetails fromUserToUserDetails(UserEntity user, Class<?> apiClass)
    {
        String username = this.getUsername(user, apiClass);
        String password = this.getPassword(user, apiClass);
        return buildUser(user, username, password);
    }

    private UserDetails buildUser(UserEntity user, String username, String password) {

        boolean enabled = true;
        boolean accountNonExpired = true;
        boolean credentialsNonExpired = true;
        boolean accountNonLocked = true;

        Collection<GrantedAuthority> authorities = getAuthoritiesFromUserEntity(user);

        User userDetails = new User(username, password, enabled,
                accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        return userDetails;
    }

    private Collection<GrantedAuthority> getAuthoritiesFromUserEntity(UserEntity user) {

        List<UserRole> roles = user.getRoles();

        if( roles == null )
            return getDefaultAuthorities();

        if( roles.isEmpty() )
            return getDefaultAuthorities();

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        for (UserRole role : roles) {
            authorities.add( new SimpleGrantedAuthority(role.getName()));
        }

        return authorities;
    }

    private Collection<GrantedAuthority> getDefaultAuthorities() {

        Collection<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
        authorities.add(new SimpleGrantedAuthority( UserRole.ROLE_USER ) );

        return authorities;
    }

    private String getUsername(UserEntity user, Class<?> apiClass ) {

      /*  if( apiClass == null ) {
            return user.getId();
        }
        else if( apiClass.equals(Foursquare.class) ) {
            return user.getFoursquareId();
        }
        else if( apiClass.equals(Facebook.class) ) {
            return user.getFacebookId();
        }  */

        return user.getId();
    }

    private String getPassword(UserEntity user, Class<?> apiClass) {

        if( apiClass == null )
            return user.getPasswordHash();
        else
            return "";
    }
}
