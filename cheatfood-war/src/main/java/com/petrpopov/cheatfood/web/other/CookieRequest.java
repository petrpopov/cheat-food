package com.petrpopov.cheatfood.web.other;

/**
 * User: petrpopov
 * Date: 18.07.13
 * Time: 11:41
 */
public class CookieRequest {
    private String cookie;

    public CookieRequest() {
    }

    public CookieRequest(String cookie) {
        this.cookie = cookie;
    }

    public String getCookie() {
        return cookie;
    }

    public void setCookie(String cookie) {
        this.cookie = cookie;
    }
}
