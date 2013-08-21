package com.petrpopov.cheatfood.web.aspects;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.web.filters.LocationFilter;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 20.08.13
 * Time: 23:01
 */

@Component
@Aspect
public class WebControllerAspect {

    @Autowired
    private LocationFilter locationFilter;

    @Autowired
    private CookieChecker cookieChecker;


    @Around("execution(* com.petrpopov.cheatfood.web.rest.LocationWebService.*(..))")
    public Object checkLocationWebService(ProceedingJoinPoint joinPoint) throws Throwable {

        return doMethodCheckAndFilterData(joinPoint);
    }

    @Around("execution(* com.petrpopov.cheatfood.web.rest.VoteWebService.*(..))")
    public Object checkVoteWebService(ProceedingJoinPoint joinPoint) throws Throwable {

        return doMethodCheckAndFilterData(joinPoint);
    }

    @Around("execution(* com.petrpopov.cheatfood.web.rest.UserWebService.*(..))")
    public Object checkUserWebService(ProceedingJoinPoint joinPoint) throws Throwable {

        return doMethodCheckAndFilterData(joinPoint);
    }

    private Object doMethodCheckAndFilterData(ProceedingJoinPoint joinPoint) throws Throwable {

        MessageResult cookieCheck = cookieChecker.checkForCookies(joinPoint);
        if( cookieCheck.getError().equals(Boolean.TRUE)) {
            return cookieCheck;
        }

        MessageResult result = new MessageResult();
        Object res;
        try {
            res = joinPoint.proceed();
            res = filterLocations(res, joinPoint);
        }
        catch (CheatException e) {
            result.setError(true);
            result.setErrorType(e.getErrorType());
            return result;
        }
        catch (AccessDeniedException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.other);
            return result;
        }

        return res;
    }

    protected Object filterLocations(Object res, ProceedingJoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();
        if( !(signature instanceof MethodSignature) )
            return res;

        MethodSignature methodSignature = (MethodSignature) signature;

        Class clazz = methodSignature.getReturnType();
        return filterLocations(res, clazz);
    }

    protected Object filterLocations(Object res, Class returnClass) {

        if( returnClass.equals(List.class)) {

            List tempList = (List) res;
            if( tempList.isEmpty() )
                return tempList;

            Object o = tempList.get(0);
            if( !(o instanceof Location) )
                return tempList;

            List<Location> list = (List<Location>) res;

            return filterLocationsList(list);
        }
        else if( returnClass.equals(MessageResult.class)) {

            MessageResult mes = (MessageResult) res;

            return filterMessageResult(mes);
        }

        return res;
    }


    protected Object filterLocationsList(List<Location> list) {

        locationFilter.filterLocations(list);

        return list;
    }

    protected Object filterMessageResult(MessageResult mes) {

        Object executionResult = mes.getResult();

        if( executionResult instanceof Location ) {
            Location location = (Location) executionResult;

            locationFilter.filterLocation(location);
        }

        return mes;
    }
}
