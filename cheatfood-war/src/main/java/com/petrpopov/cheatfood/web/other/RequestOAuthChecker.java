package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.connection.GenericConnectionService;
import org.springframework.social.connect.Connection;
import org.springframework.social.oauth1.OAuthToken;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 15:40
 */

@Component
public class RequestOAuthChecker {

    public static final String OAUTH_VERIFIER = "oauth_verifier";
    public static final String CODE = "code";
    public static final String USER_ID = "userId";
    public static final String API_CONNECTION = "apiConnection";
    public static final String API_CLASS = "apiClass";

    public EmailCallbackParams getParams(@Valid EmailCallback emailCallback, NativeWebRequest request, HttpServletResponse response) {

        EmailCallbackParams params = new EmailCallbackParams();

        OAuthToken requestToken = (OAuthToken) request.getAttribute(GenericConnectionService.OAUTH_TOKEN_ATTRIBUTE,
                RequestAttributes.SCOPE_SESSION);
        request.removeAttribute(GenericConnectionService.OAUTH_TOKEN_ATTRIBUTE, RequestAttributes.SCOPE_SESSION);
        if( requestToken == null ) {
            params.setValid(Boolean.FALSE);
            return params;
        }

        String oauth_verifier = (String) request.getAttribute(OAUTH_VERIFIER, RequestAttributes.SCOPE_SESSION);
        if( oauth_verifier != null ) {
            request.removeAttribute(OAUTH_VERIFIER, RequestAttributes.SCOPE_SESSION);
            params.setType(EmailCallbackType.oauth_verifier);
            params.setValid(checkParamsOAuth1(oauth_verifier, emailCallback));
        }

        String code = (String) request.getAttribute(CODE, RequestAttributes.SCOPE_SESSION);
        if( code != null ) {
            request.removeAttribute(CODE, RequestAttributes.SCOPE_SESSION);
            params.setType(EmailCallbackType.code);
            params.setValid(checkParamsOAuth2(code, emailCallback));
        }

        Connection<?> apiConnection = (Connection<?>) request.getAttribute(API_CONNECTION, RequestAttributes.SCOPE_SESSION);
        if( apiConnection != null )
            request.removeAttribute(API_CONNECTION, RequestAttributes.SCOPE_SESSION);

        Class<?> apiClass = (Class<?>) request.getAttribute(API_CLASS, RequestAttributes.SCOPE_SESSION);
        if( apiClass != null )
            request.removeAttribute(API_CLASS, RequestAttributes.SCOPE_SESSION);

        params.setApiClass(apiClass);
        params.setConnection(apiConnection);
        params.setEmailCallback(emailCallback);
        params.setRequest(request);
        params.setResponse(response);

        return params;
    }

    private Boolean checkParamsOAuth1(String oauth_verifier, EmailCallback emailCallback) {

        Boolean res = Boolean.FALSE;
        if( emailCallback == null )
            return res;

        if( emailCallback.getOauth_verifier() == null)
            return res;

        if( oauth_verifier == null )
            return res;

        if( oauth_verifier.equals(emailCallback.getOauth_verifier()))
            return Boolean.TRUE;

        return res;
    }

    private Boolean checkParamsOAuth2(String code, EmailCallback emailCallback) {

        Boolean res = Boolean.FALSE;
        if( emailCallback == null )
            return res;

        if( emailCallback.getOauth_verifier() == null)
            return res;

        if( code == null )
            return res;

        if( code.equals(emailCallback.getCode()))
            return Boolean.TRUE;

        return res;
    }
}
