package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.ConnectionAccessTokenFieldHandler;
import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.Connection;
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
    private ConnectionAccessTokenFieldHandler connectionAccessTokenFieldHandler;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    public Authentication authenticate(Connection connection)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassByConnection(connection);

        String token = connectionAccessTokenFieldHandler.getAccessTokenFromConnection(connection);
        Authentication authentication = this.doAuthenticate(connection.getKey().getProviderUserId(), token, apiClass);

        return authentication;
    }

    public Authentication authenticate(String username, String password)
    {
        Authentication authentication = this.doAuthenticate(username, password, null);

        return authentication;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response)
    {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("anonymous", "anonymous",
                new ArrayList(Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        rememberMeServices.cancelCookie(request, response);
    }

    private Authentication doAuthenticate(String username, String token, Class<?> apiClass)
    {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token);

        if( apiClass != null )
            authenticationToken.setDetails(apiClass);

        Authentication authentication = authenticationManager.authenticate( authenticationToken );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

}
