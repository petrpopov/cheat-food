package com.petrpopov.cheatfood.web.aspects;

import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 21.08.13
 * Time: 1:24
 */

@Component
public class CookieChecker {

    @Autowired
    private CookieService cookieService;

    public MessageResult checkForCookies(ProceedingJoinPoint joinPoint) {

        MessageResult result = new MessageResult();
        CookieRequest cookieRequest = null;

        Object[] args = joinPoint.getArgs();
        if( args == null )
            return result;

        for (Object arg : args) {
            if( arg instanceof CookieRequest ) {
                cookieRequest = (CookieRequest) arg;
                break;
            }
        }

        if( cookieRequest == null )
            return result;

        boolean valid = cookieService.isCookieValidForCurrentUser(cookieRequest);
        if( !valid ) {
            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
        }

        return result;
    }
}
