package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.security.web.authentication.rememberme.TokenBasedRememberMeServices;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.foursquare.api.Foursquare;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:21
 */

public class CheatRememberMeServices extends TokenBasedRememberMeServices {

    private String cookieName = "CHEATFOOD";
    private String cookieName_name = "name";
    private int cookieSize = 4;

    @Autowired
    private UserDetailsFieldHandler userDetailsFieldHandler;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    public CheatRememberMeServices(String key, UserDetailsService userDetailsService) {
        super(key, userDetailsService);
    }


    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String username = retrieveUserName(authentication);
        String token = retrievePassword(authentication);
        String type = "default";

        if( authentication instanceof UsernamePasswordAuthenticationToken ) {
            Object details = authentication.getDetails();

            if( details instanceof Class<?> ) {
                Class<?> clazz = (Class<?>) details;
                String providerId = providerIdClassStorage.getProviderIdByClass(clazz);

                if( providerId != null )
                    type = providerId;
            }
        }

        long expiryTime = System.currentTimeMillis() + (1000L* TWO_WEEKS_S);

        setCookie(new String[] {username, Long.toString(expiryTime), token, type}, (int)expiryTime, request, response);
    }

    @Override
    protected void setCookie(String[] tokens, int maxAge, HttpServletRequest request, HttpServletResponse response) {

        String value = this.encodeCookie(tokens);

        Cookie cookie = new Cookie(cookieName, value );
        cookie.setMaxAge(maxAge);
        cookie.setPath(getCookiePath(request));

        response.addCookie(cookie);

        Cookie name = new Cookie(cookieName_name, tokens[0]);
        name.setMaxAge(maxAge);
        name.setPath(getCookiePath(request));
        response.addCookie(name);
    }


    public Authentication autoLoginWithCookie(HttpServletRequest request, HttpServletResponse response) {

        String rememberMeCookie = extractRememberMeCookie(request);

        if (rememberMeCookie == null) {
            return null;
        }

        if (rememberMeCookie.length() == 0) {
            cancelCookie(request, response);
            return null;
        }

        UserDetails user = null;

        try {
            String[] cookieTokens = decodeCookie(rememberMeCookie);
            user = processAutoLoginCookie(cookieTokens, request, response);

            if( user == null )
                return null;

            return createSuccessfulAuthentication(request, user);
        }
        catch (Exception e) {}

        cancelCookie(request, response);
        return null;
    }

    @Override
    protected String extractRememberMeCookie(HttpServletRequest request)
    {
        Cookie[] cookies = request.getCookies();

        if ((cookies == null) || (cookies.length == 0)) {
            return null;
        }

        for (Cookie cookie : cookies) {
            if (cookieName.equals(cookie.getName())) {
                return cookie.getValue();
            }
        }

        return null;
    }

    @Override
    protected UserDetails processAutoLoginCookie(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

        if (cookieTokens.length != cookieSize) {
            throw new InvalidCookieException("Cookie token did not contain 3" +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        String username = cookieTokens[0];

        //load by main id, becuase we saved in cookie real id, not facebook or other social network id
        UserDetails userDetails = this.getUserDetailsService().loadUserByUsername(username);

        String token = cookieTokens[2];
        userDetailsFieldHandler.setPassword( userDetails, token );

        String type = cookieTokens[3];
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(type);
        boolean exists = checkConnectionsForUser(username, apiClass);
        if( !exists )
            return null;

        return userDetails;
    }


    @Override
    public void cancelCookie(HttpServletRequest request, HttpServletResponse response)
    {
        Cookie cookie = new Cookie(cookieName, null);
        cookie.setMaxAge(0);
        cookie.setPath(getCookiePath(request));

        response.addCookie(cookie);

        Cookie name = new Cookie(cookieName_name, null);
        name.setMaxAge(0);
        name.setPath(getCookiePath(request));

        response.addCookie(cookie);
    }

    protected boolean checkConnectionsForUser(String username, Class<?> apiClass)
    {
        try {
            ConnectionRepository connectionRepository = usersConnectionRepository.createConnectionRepository(username);
            Connection<?> connection = connectionRepository.getPrimaryConnection(apiClass);

            if(connection != null )
                return true;
        }
        catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    protected String retrieveUserName(Authentication authentication) {

        if( authentication instanceof UsernamePasswordAuthenticationToken ) {
            Object details = authentication.getDetails();

            if( details instanceof Class<?> ) {
                Class<?> clazz = (Class<?>) details;

                String id = ((UserDetails) authentication.getPrincipal()).getUsername();
                if( clazz.equals(Foursquare.class) ) {
                    UserEntity userEntity = userService.getUserByFoursquareId(id);
                    return userEntity.getId();
                }
                else if( clazz.equals(Facebook.class) ) {
                    UserEntity userEntity = userService.getUserByFacebookId(id);
                    return userEntity.getId();
                }
                return id;
            }
            else
                return super.retrieveUserName(authentication);
        }
        else
            return super.retrieveUserName(authentication);
    }

    private String getCookiePath(HttpServletRequest request) {
        String contextPath = request.getContextPath();
        return contextPath.length() > 0 ? contextPath : "/";
    }

    public void setCookieName(String cookieName) {
        this.cookieName = cookieName;
    }

    public String getCookieName() {
        return cookieName;
    }
}
