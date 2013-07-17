package com.petrpopov.cheatfood.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.RememberMeAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.GenericFilterBean;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 18:37
 */
@Component
public class CheatRememberFilter extends GenericFilterBean{

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private UsernameAuthenticationTokenService usernameAuthenticationTokenService;

    public CheatRememberFilter() {
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;


        if (SecurityContextHolder.getContext().getAuthentication() != null)
        {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        //TODO: this is a pretty shitty code
        RememberMe rememberMe = rememberMeServices.autoLoginWithCookie(request, response);
        if( rememberMe == null ) {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        Authentication rememberMeAuth = rememberMe.getAuthentication();
        if (rememberMeAuth == null)
        {
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        try
        {
            if(rememberMeAuth instanceof RememberMeAuthenticationToken)
            {
               // RememberMeAuthenticationToken t = (RememberMeAuthenticationToken) rememberMeAuth;
                //UsernamePasswordAuthenticationToken token = usernameAuthenticationTokenService.getUsernamePasswordToken(t);

                UsernamePasswordAuthenticationToken token = rememberMe.getToken();

                rememberMeAuth = authenticationManager.authenticate(token);
                SecurityContextHolder.getContext().setAuthentication(rememberMeAuth);

                filterChain.doFilter(request, response);
            }
            else
                rememberMeServices.loginFail(request, response);
        }
        catch (AuthenticationException authenticationException) {
            rememberMeServices.loginFail(request, response);
        }
    }

}
