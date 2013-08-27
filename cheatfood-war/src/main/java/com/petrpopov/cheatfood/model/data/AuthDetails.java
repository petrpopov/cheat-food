package com.petrpopov.cheatfood.model.data;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 17:58
 */
public class AuthDetails {

    private Class<?> apiClass;
    private Boolean firstTimeLogin;

    public AuthDetails() {
        this.firstTimeLogin = Boolean.FALSE;
    }

    public AuthDetails(Class<?> apiClass, Boolean firstTimeLogin) {
        this.apiClass = apiClass;

        if( firstTimeLogin == null )
            this.firstTimeLogin = Boolean.FALSE;
        else
            this.firstTimeLogin = firstTimeLogin;
    }

    public Class<?> getApiClass() {
        return apiClass;
    }

    public void setApiClass(Class<?> apiClass) {
        this.apiClass = apiClass;
    }

    public Boolean getFirstTimeLogin() {
        return firstTimeLogin;
    }

    public void setFirstTimeLogin(Boolean firstTimeLogin) {
        this.firstTimeLogin = firstTimeLogin;
    }
}
