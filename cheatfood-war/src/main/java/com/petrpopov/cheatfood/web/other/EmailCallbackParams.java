package com.petrpopov.cheatfood.web.other;

import org.springframework.social.connect.Connection;
import org.springframework.web.context.request.NativeWebRequest;

import javax.servlet.http.HttpServletResponse;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 13:33
 */
public class EmailCallbackParams {

    private Boolean valid = Boolean.FALSE;
    private EmailCallbackType type;
    private EmailCallback emailCallback;
    private Connection<?> connection;
    private Class<?> apiClass;
    private NativeWebRequest request;
    private HttpServletResponse response;

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public EmailCallbackType getType() {
        return type;
    }

    public void setType(EmailCallbackType type) {
        this.type = type;
    }

    public EmailCallback getEmailCallback() {
        return emailCallback;
    }

    public void setEmailCallback(EmailCallback emailCallback) {
        this.emailCallback = emailCallback;
    }

    public Connection<?> getConnection() {
        return connection;
    }

    public void setConnection(Connection<?> connection) {
        this.connection = connection;
    }

    public Class<?> getApiClass() {
        return apiClass;
    }

    public void setApiClass(Class<?> apiClass) {
        this.apiClass = apiClass;
    }

    public NativeWebRequest getRequest() {
        return request;
    }

    public void setRequest(NativeWebRequest request) {
        this.request = request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    public void setResponse(HttpServletResponse response) {
        this.response = response;
    }
}
