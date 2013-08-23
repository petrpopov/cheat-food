package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import com.petrpopov.cheatfood.web.other.SocialAccessError;
import com.petrpopov.cheatfood.web.other.SocialConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @RequestMapping(value="connect/{providerId}", method=RequestMethod.GET, params="code")
    public RedirectView providerApiCallbackOAuth2(@PathVariable String providerId,
                                                  @RequestParam("code") String code,
                                                  NativeWebRequest request, HttpServletResponse response)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        socialConnectionService.apiCallback(code, null, apiClass, request, response);

        return new RedirectView("/", true);
    }

    @RequestMapping(value="connect/{providerId}", method=RequestMethod.GET, params="oauth_verifier")
    public RedirectView providerApiCallbackOAuth1(@PathVariable String providerId,
                                                  @RequestParam("oauth_verifier") String oauth_verifier,
                                                  NativeWebRequest request, HttpServletResponse response)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        socialConnectionService.apiCallback(null, oauth_verifier, apiClass, request, response);

        return new RedirectView("/", true);
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
