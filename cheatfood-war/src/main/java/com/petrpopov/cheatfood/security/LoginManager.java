package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.ConnectionAccessTokenFieldHandler;
import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.model.data.AuthDetails;
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
        return this.authenticate(connection, null);
    }

    public Authentication authenticate(Connection connection, Boolean firstTimeLogin)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassByConnection(connection);

        String token = connectionAccessTokenFieldHandler.getAccessTokenFromConnection(connection);
        Authentication authentication = this.doAuthenticate(connection.getKey().getProviderUserId(), token, apiClass, firstTimeLogin);

        return authentication;
    }

    public Authentication authenticate(String username, String password)
    {
        return this.authenticate(username, password, null);
    }

    public Authentication authenticate(String username, String password, Boolean firstTimeLogin)
    {
        Authentication authentication = this.doAuthenticate(username, password, null, firstTimeLogin);

        return authentication;
    }

    public void logout(HttpServletRequest request, HttpServletResponse response)
    {
        AnonymousAuthenticationToken anonymous = new AnonymousAuthenticationToken("anonymous", "anonymous",
                new ArrayList(Arrays.asList(new SimpleGrantedAuthority("ROLE_ANONYMOUS"))));
        SecurityContextHolder.getContext().setAuthentication(anonymous);

        rememberMeServices.cancelCookie(request, response);
    }

    private Authentication doAuthenticate(String username, String token, Class<?> apiClass, Boolean firstTimeLogin)
    {
        UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token);

        if( apiClass != null ) {
            authenticationToken.setDetails(new AuthDetails(apiClass, firstTimeLogin));
        }
        else {
            AuthDetails details = new AuthDetails();
            details.setFirstTimeLogin(firstTimeLogin);
            authenticationToken.setDetails(details);
        }

        Authentication authentication = authenticationManager.authenticate( authenticationToken );
        SecurityContextHolder.getContext().setAuthentication(authentication);

        return authentication;
    }

}
