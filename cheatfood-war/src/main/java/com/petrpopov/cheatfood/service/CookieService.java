package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.security.LoginManager;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.codec.Base64;
import org.springframework.security.web.authentication.rememberme.InvalidCookieException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * User: petrpopov
 * Date: 18.07.13
 * Time: 11:45
 */

@Component
public class CookieService {

    private static final String DELIMITER = ":";
    private int cookieSize = 4;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private LoginManager loginManager;

    public boolean isCookieValidForCurrentUser(CookieRequest cookieRequest, HttpServletRequest request, HttpServletResponse response) throws CheatException {

        UserEntity userEntity = userContextHandler.currentContextUser();

        try {
            checkCookie(cookieRequest, userEntity);
        }
        catch (CheatException e) {
            //something wrong with cookies
            //but we have authorized user in this context
            if( userEntity != null )
                loginManager.logout(request, response);

            throw e;
        }

        return true;
    }

    private void checkCookie(CookieRequest cookieRequest, UserEntity userEntity) throws CheatException {
        if( cookieRequest == null )
            throw new CheatException("Cookie is null");

        String cookie = cookieRequest.getCookie();
        if( cookie == null )
            throw new CheatException("Cookie is null");

        if( cookie.isEmpty() )
            throw new CheatException("Cookie is empty");

        try {
            String[] strings = decodeCookie(cookie);

            if (strings.length != cookieSize) {
                throw new CheatException("Cookie token did not contain 3" +
                        " tokens, but contained '" + Arrays.asList(strings) + "'");
            }

            if(userEntity == null)
                throw new CheatException("No authorized user!");

            String username = strings[0];
            if( !userEntity.getId().equals(username) )
                throw new CheatException("This is cookie from another user!");

            //additional checks
            //user is valid, user is not blocked, user have social connections
            //user password is valid etc
        }
        catch (Exception e) {
            throw new CheatException("Cannot decode cookie!");
        }
    }

    protected String[] decodeCookie(String cookieValue) throws InvalidCookieException {
        for (int j = 0; j < cookieValue.length() % 4; j++) {
            cookieValue = cookieValue + "=";
        }

        if (!Base64.isBase64(cookieValue.getBytes())) {
            throw new InvalidCookieException( "Cookie token was not Base64 encoded; value was '" + cookieValue + "'");
        }

        String cookieAsPlainText = new String(Base64.decode(cookieValue.getBytes()));

        String[] tokens = StringUtils.delimitedListToStringArray(cookieAsPlainText, DELIMITER);

        if ((tokens[0].equalsIgnoreCase("http") || tokens[0].equalsIgnoreCase("https")) && tokens[1].startsWith("//")) {
            // Assume we've accidentally split a URL (OpenID identifier)
            String[] newTokens = new String[tokens.length - 1];
            newTokens[0] = tokens[0] + ":" + tokens[1];
            System.arraycopy(tokens, 2, newTokens, 1, newTokens.length - 1);
            tokens = newTokens;
        }

        return tokens;
    }
}
