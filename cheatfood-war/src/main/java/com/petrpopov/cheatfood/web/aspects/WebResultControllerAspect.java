package com.petrpopov.cheatfood.web.aspects;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.ErrorType;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.web.filters.LocationFilter;
import com.petrpopov.cheatfood.web.filters.UserEntityFilter;
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
public class WebResultControllerAspect {

    @Autowired
    private LocationFilter locationFilter;

    @Autowired
    private UserEntityFilter userEntityFilter;

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
            res = filterResultFields(res, joinPoint);
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

    protected Object filterResultFields(Object res, ProceedingJoinPoint joinPoint) {

        Signature signature = joinPoint.getSignature();
        if( !(signature instanceof MethodSignature) )
            return res;

        MethodSignature methodSignature = (MethodSignature) signature;

        Class clazz = methodSignature.getReturnType();
        return filterResultFields(res, clazz);
    }

    protected Object filterResultFields(Object res, Class returnClass) {

        if( returnClass.equals(List.class)) {

            List tempList = (List) res;
            if( tempList.isEmpty() )
                return tempList;

            Object o = tempList.get(0);
            if( o instanceof Location) {
                List<Location> list = (List<Location>) res;

                return filterLocationsList(list);
            }
            else if( o instanceof UserEntity ) {
                List<UserEntity> list = (List<UserEntity>) res;

                return filterUserEntityList(list);
            }

            return tempList;
        }
        else if( returnClass.equals(MessageResult.class)) {

            MessageResult mes = (MessageResult) res;

            return filterMessageResult(mes);
        }

        return filterObject(res);
    }

    protected Object filterMessageResult(MessageResult mes) {

        Object res = mes.getResult();

        filterObject(res);

        return mes;
    }

    protected Object filterObject(Object res) {

        if( res == null )
            return res;

        if( res instanceof Location ) {
            Location location = (Location) res;

            locationFilter.filterLocation(location);
        }
        else if( res instanceof UserEntity ) {
            UserEntity entity = (UserEntity) res;

            userEntityFilter.filterUserEntity(entity);
        }

        return res;
    }

    protected Object filterLocationsList(List<Location> list) {

        locationFilter.filterLocations(list);

        return list;
    }

    protected Object filterUserEntityList(List<UserEntity> list) {

        userEntityFilter.filterUserEntities(list);

        return list;
    }

}
