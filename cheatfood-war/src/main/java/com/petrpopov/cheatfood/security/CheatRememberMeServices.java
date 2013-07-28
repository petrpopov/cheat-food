package com.petrpopov.cheatfood.security;

import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import org.springframework.social.twitter.api.Twitter;
import org.springframework.stereotype.Component;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * User: petrpopov
 * Date: 14.02.13
 * Time: 10:21
 */

@Component
public class CheatRememberMeServices extends TokenBasedRememberMeServices {

    @Value("#{properties.cookie_name}")
    private String cookieName;
    private String cookieName_name = "name";
    private String defaultProviderType = "default";
    private int cookieSize = 4;

    @Autowired
    private UserDetailsFieldHandler userDetailsFieldHandler;

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private UserDetailsAssembler userDetailsAssembler;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    @Autowired
    public CheatRememberMeServices(UserDetailsService userDetailsService) {
        super("cheatfood", userDetailsService);
    }


    @Override
    public void onLoginSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        String username = retrieveUserName(authentication);
        String token = retrievePassword(authentication);
        String providerType = defaultProviderType;

        if( authentication instanceof UsernamePasswordAuthenticationToken ) {
            Object details = authentication.getDetails();

            if( details instanceof Class<?> ) {
                Class<?> clazz = (Class<?>) details;
                String providerId = providerIdClassStorage.getProviderIdByClass(clazz);

                if( providerId != null )
                    providerType = providerId;
            }
        }

        long expiryTime = System.currentTimeMillis() + (1000L* TWO_WEEKS_S);

        setCookie(new String[] {username, Long.toString(expiryTime), token, providerType}, (int)expiryTime, request, response);
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


    public RememberMe autoLoginWithCookie(HttpServletRequest request, HttpServletResponse response) {

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

            //TODO: this is a pretty shitty code
            UsernamePasswordAuthenticationToken token = processCookies(cookieTokens, request, response);

            if( user == null )
                return null;

            Authentication authentication = createSuccessfulAuthentication(request, user);
            RememberMe rememberMe = new RememberMe();
            rememberMe.setAuthentication(authentication);
            rememberMe.setToken(token);

            return rememberMe;
        }
        catch (Exception e) {
            e.printStackTrace();
        }

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

        //username is alwasy a real mongodb-id
        UserEntity entity = userService.getUserById(username);
        if( entity == null )
            return null;

        UserDetails userDetails;
        String providerType = cookieTokens[3];

        if( !providerType.equals(defaultProviderType) ) {
            Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerType);

            userDetails = userDetailsAssembler.fromUserToUserDetails(entity, apiClass);

            boolean exists = checkConnectionsForUser(entity, apiClass);
            if( !exists )
                return null;
        }
        else {
            userDetails = this.getUserDetailsService().loadUserByUsername(username);

            String token = cookieTokens[2];
            userDetailsFieldHandler.setPassword( userDetails, token );
        }


        return userDetails;
    }

    protected UsernamePasswordAuthenticationToken processCookies(String[] cookieTokens, HttpServletRequest request, HttpServletResponse response) {

        if (cookieTokens.length != cookieSize) {
            throw new InvalidCookieException("Cookie token did not contain 3" +
                    " tokens, but contained '" + Arrays.asList(cookieTokens) + "'");
        }

        String c_username = cookieTokens[0];

        //username is alwasy a real mongodb-id
        UserEntity entity = userService.getUserById(c_username);

        String providerType = cookieTokens[3];
        if( !providerType.equals(defaultProviderType) ) {

            String token = null;
            String username = null;

            Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerType);
            if( apiClass.equals(Facebook.class) ) {
                token = entity.getFacebookToken();
                username = entity.getFacebookId();
            }
            else if( apiClass.equals(Foursquare.class)) {
                token = entity.getFoursquareToken();
                username = entity.getFoursquareId();
            }
            else if( apiClass.equals(Twitter.class)) {
                token = entity.getTwitterToken();
                username = entity.getTwitterToken();
            }

            if( apiClass != null ) {
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token);
                authenticationToken.setDetails(apiClass);
                return authenticationToken;
            }
            else {
                username = c_username;
                token = entity.getPasswordHash();
                UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(username, token);
                return authenticationToken;
            }
        }
        else {
            String token = entity.getPasswordHash();
            UsernamePasswordAuthenticationToken authenticationToken = new UsernamePasswordAuthenticationToken(c_username, token);
            return authenticationToken;
        }
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

        response.addCookie(name);
    }

    protected boolean checkConnectionsForUser(UserEntity userEntity, Class<?> apiClass)
    {
        if( apiClass == null )
            return false;

        //real _id, because we previously save a connection with ConnectionRepository bean with real _id
        //not with providerUserId (SocialConfig.java)
        String username = userEntity.getId();

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

           /* if( details instanceof Class<?> ) {
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
                return super.retrieveUserName(authentication);  */
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
