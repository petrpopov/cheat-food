package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.security.CheatRememberMeServices;
import com.petrpopov.cheatfood.security.LoginManager;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: petrpopov
 * Date: 21.08.13
 * Time: 1:41
 */

@Component
public class BaseWebService {

    @Autowired
    private LoginManager loginManager;

    @Autowired
    private CheatRememberMeServices rememberMeServices;

    protected MessageResult checkIfLocationExists(Location location) {

        MessageResult result = new MessageResult();
        if( location == null ) {
            result.setError(true);
            result.setErrorType(ErrorType.unknown_location);
            result.setMessage("There is no such location!");

            return result;
        }

        return result;
    }

    protected Authentication authenticate(String name, String password, HttpServletRequest request, HttpServletResponse response) throws CheatException {

        try {
            Authentication authenticate = loginManager.authenticate(name, password);
            rememberMeServices.onLoginSuccess(request, response, authenticate);
            return authenticate;
        }
        catch (Exception e) {
            throw new CheatException(ErrorType.login_error);
        }
    }
}
