package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.connection.UserEmailInfo;
import com.petrpopov.cheatfood.web.other.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.io.IOException;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 11:44
 */

@Controller
public class SocialController {

    @Autowired
    private SocialConnectionService socialConnectionService;

    @Autowired
    private ProviderIdClassStorage providerIdClassStorage;

    @Autowired
    private RequestOAuthChecker requestOAuthChecker;


    @RequestMapping(value="connect/{providerId}", method= RequestMethod.POST)
    public RedirectView providerConnect(@PathVariable String providerId, NativeWebRequest request) {

        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        String scope = null;
        if( apiClass.equals(Facebook.class) ) {
            scope = this.getScope(request);
        }

        String url = socialConnectionService.getAuthorizeUrl(apiClass, scope, request);

        return new RedirectView(url);
    }


    @RequestMapping(value="connect/{providerId}", method=RequestMethod.GET, params="oauth_verifier")
    public ModelAndView providerApiCallbackOAuth1(@PathVariable String providerId,
                                                  @RequestParam("oauth_verifier") String oauth_verifier,
                                                  NativeWebRequest request, HttpServletResponse response)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        UserEmailInfo info = socialConnectionService.apiCallbackOAuth1(oauth_verifier, apiClass, request, response);
        if(info == null) {
            return new ModelAndView(new RedirectView("/errors/404", true));
        }

        if( info.getEmail() == null ) {

            ModelAndView res = new ModelAndView("email");

            res.addObject(RequestOAuthChecker.OAUTH_VERIFIER, oauth_verifier);
            res.addObject(RequestOAuthChecker.USER_ID, info.getUserId());
            request.setAttribute(RequestOAuthChecker.OAUTH_VERIFIER, oauth_verifier, RequestAttributes.SCOPE_SESSION);

            return res;
        }

        return new ModelAndView(new RedirectView("/", true));
    }

    @RequestMapping(value="connect/{providerId}", method=RequestMethod.GET, params="code")
    public ModelAndView providerApiCallbackOAuth2(@PathVariable String providerId,
                                                  @RequestParam("code") String code,
                                                  NativeWebRequest request, HttpServletResponse response)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        UserEmailInfo info = socialConnectionService.apiCallbackOAuth2(code, apiClass, request, response);
        if( info.getEmail() == null ) {

            ModelAndView res = new ModelAndView("email");

            res.addObject(RequestOAuthChecker.CODE, code);
            res.addObject(RequestOAuthChecker.USER_ID, info.getUserId());
            request.setAttribute(RequestOAuthChecker.CODE, code, RequestAttributes.SCOPE_SESSION);

            return res;
        }

        return new ModelAndView(new RedirectView("/", true));
    }

    @RequestMapping(value = "connect/email/{userId}", method=RequestMethod.GET, params = "ok")
    public ModelAndView providerEmailCallback(@PathVariable String userId, @ModelAttribute("emailCallback") @Valid EmailCallback emailCallback,
                                              NativeWebRequest request, HttpServletResponse response) {


        EmailCallbackParams params = requestOAuthChecker.getParams(emailCallback, request, response);
        if( params.getValid().equals(Boolean.FALSE))
            return new ModelAndView(new RedirectView("/", true));

        if( params.getType().equals(EmailCallbackType.oauth_verifier)) {
            return performOAuth1EmailCallback(userId, params);
        }
        else if( params.getType().equals(EmailCallbackType.code)) {
            return performOAuth2EmailCallback(userId, params);
        }


        return new ModelAndView(new RedirectView("/", true));
    }

    @RequestMapping(value = "connect/email/{userId}", method=RequestMethod.GET, params = "cancel")
    public ModelAndView providerEmailCancel(@PathVariable String userId, @ModelAttribute("emailCallback") EmailCallback emailCallback,
                                            NativeWebRequest request, HttpServletResponse response) {

        EmailCallbackParams params = requestOAuthChecker.getParams(emailCallback, request, response);
        if( params.getValid().equals(Boolean.FALSE))
            return new ModelAndView(new RedirectView("/", true));

        socialConnectionService.deAuthorize(userId, request, response);

        return new ModelAndView(new RedirectView("/", true));
    }

    private ModelAndView performOAuth1EmailCallback(String userId, EmailCallbackParams params) {

        UserEmailInfo info = new UserEmailInfo();
        info.setNewUser(Boolean.TRUE);
        info.setEmail(params.getEmailCallback().getEmailConnect());
        info.setUserId(userId);

        socialConnectionService.authorize(info, params.getConnection(), params.getApiClass(), params.getRequest(), params.getResponse());

        return new ModelAndView(new RedirectView("/", true));
    }

    private ModelAndView performOAuth2EmailCallback(String userId, EmailCallbackParams params) {

        UserEmailInfo info = new UserEmailInfo();
        info.setNewUser(Boolean.TRUE);
        info.setEmail(params.getEmailCallback().getEmailConnect());
        info.setUserId(userId);

        socialConnectionService.authorize(info, params.getConnection(), params.getApiClass(), params.getRequest(), params.getResponse());

        return new ModelAndView(new RedirectView("/", true));
    }

    @RequestMapping(value = "connect/{providerId}", method = RequestMethod.GET)
    public RedirectView accessDenied(@PathVariable String providerId, SocialAccessError error) throws IOException {

        return new RedirectView("/", true);
    }

    @RequestMapping(value="connect/logout", method=RequestMethod.GET)
    @ResponseBody
    public RedirectView logout(HttpServletRequest request, HttpServletResponse response) {

        socialConnectionService.logout(request, response);

        return new RedirectView("/", true);
    }

    private String getScope(NativeWebRequest request) {

        String scope = request.getParameter("scope");
        return scope;
    }
}
