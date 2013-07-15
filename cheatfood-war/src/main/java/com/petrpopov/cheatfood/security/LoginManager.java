package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.FoursquareConnectionFieldHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.support.OAuth2Connection;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * User: petrpopov
 * Date: 14.02.2013
 * Time: 11:28
 */

@Component
public class LoginManager {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private FoursquareConnectionFieldHandler foursquareConnectionFieldHandler;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    public Authentication authenticate(Connection connection)
    {
        if( !(connection instanceof OAuth2Connection) )
            return null;

        String token = foursquareConnectionFieldHandler.getAccessTokenFromConnection((OAuth2Connection) connection);
        Authentication authentication = this.authenticate(connection.getKey().getProviderUserId(), token );

        return authentication;
    }

    public Authentication authenticate(String username, String token)
    {
        Authentication authentication = authenticationManager.authenticate( new UsernamePasswordAuthenticationToken(username, token) );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response)
    {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("anonymous", "anonymous",
                new ArrayList(Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        rememberMeServices.cancelCookie(request, response);
    }

}
