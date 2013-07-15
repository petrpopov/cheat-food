package com.petrpopov.cheatfood.web;

import com.petrpopov.cheatfood.connection.FoursquareDefaultBean;
import org.apache.log4j.Logger;
import org.springframework.aop.framework.Advised;
import org.springframework.aop.support.AopUtils;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.lang.reflect.Field;

/**
 * User: petrpopov
 * Date: 19.02.13
 * Time: 22:39
 */
public class SocialConnectionInterceptor extends HandlerInterceptorAdapter {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if(! (handler instanceof HandlerMethod))
            return super.preHandle(request, response, handler);

        HandlerMethod method = (HandlerMethod) handler;
        Object bean = method.getBean();

        boolean result = checkFoursquareConnection(bean);

        if( result )
            return super.preHandle(request, response, handler);
        else {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/login");
            dispatcher.forward(request, response);
            return false;
        }
    }

    private boolean checkFoursquareConnection(Object bean) {

        if(bean == null)
            return false;

        Object api = this.getApiObject(bean, Foursquare.class);
        return this.checkApiObject(api);
    }

    private Object getApiObject(Object bean, Class requiredClass) {

        Field f = this.getField(bean, requiredClass);

        if( f == null )
            return null;

        Object api = null;
        f.setAccessible(true);
        try {
            api = f.get(bean);
        }
        catch (Exception e) {
            return null;
        }

        return api;
    }

    private boolean checkApiObject(Object api) {

        Object target = unproxyApi(api);

        if( target instanceof FoursquareDefaultBean) {
            Logger.getLogger(SocialConnectionInterceptor.class).info("NOT CONNECTED TO FOURSQUARE !");
            return false;
        }

        return true;
    }

    private Object unproxyApi(Object api) {

        if( api == null ) {
            Logger.getLogger(SocialConnectionInterceptor.class).info("API IS NULL");
            return null;
        }

        if(!AopUtils.isAopProxy(api))
            return null;

        if( !(api instanceof Advised)) {
            return null;
        }

        Object target = null;
        try {
            target = ((Advised)api).getTargetSource().getTarget();
        } catch (Exception e) {
            return null;
        }

        return target;
    }

    private Field getField(Object bean, Class requiredClass) {

        if( bean == null )
            return null;

        Field[] fields = bean.getClass().getDeclaredFields();

        Field f = null;
        boolean ok = false;
        for(int i = 0; i < fields.length; i++) {
            f = fields[i];
            Class<?> type = f.getType();

            if( type.equals(requiredClass) ) {
                ok = true;
                break;
            }
        }

        if(!ok)
            return null;

        return f;
    }
}
