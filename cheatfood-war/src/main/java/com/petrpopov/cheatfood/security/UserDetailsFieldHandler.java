package com.petrpopov.cheatfood.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;

/**
 * User: petrpopov
 * Date: 18.02.13
 * Time: 13:08
 */
@Component
public class UserDetailsFieldHandler {


    public void setPassword(UserDetails userDetails, String token)
    {
        if( !(userDetails instanceof User) )
            return;

        User user = (User) userDetails;
        try {

            Field field = user.getClass().getDeclaredField("password");
            field.setAccessible(true);
            field.set(user, token);

        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public void setPassword(UserDetails userDetails, Authentication authentication)
    {
        this.setPassword(userDetails, authentication.getCredentials().toString() );
    }

}
