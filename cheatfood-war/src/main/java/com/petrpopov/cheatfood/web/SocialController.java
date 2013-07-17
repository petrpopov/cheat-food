package com.petrpopov.cheatfood.web;

import com.petrpopov.cheatfood.connection.ProviderIdClassStorage;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
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

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping(value="connect/{providerId}", method= RequestMethod.POST)
    public RedirectView foursquareConnect(@PathVariable String providerId, HttpServletRequest request) {

        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        String scope = null;
        if( apiClass.equals(Facebook.class) ) {
            scope = this.getScope(request);
        }

        String url = socialConnectionService.getAuthorizeUrl(apiClass, scope);

        return new RedirectView(url);
    }

    @RequestMapping(value="connect/{providerId}", method=RequestMethod.GET, params="code")
    public RedirectView foursquareCallback(@PathVariable String providerId, @RequestParam("code") String code,
                                           HttpServletRequest request, HttpServletResponse response)
    {
        Class<?> apiClass = providerIdClassStorage.getProviderClassById(providerId);

        socialConnectionService.apiCallback(code, apiClass, request, response);

        return new RedirectView("/", true);
    }

    @RequestMapping(value = "connect/{providerId}", method = RequestMethod.GET)
    public RedirectView accessDenied(@PathVariable String providerId, SocialAccessError error) throws IOException {

        return new RedirectView("/", true);
    }

    @RequestMapping(value="connect/logout", method=RequestMethod.DELETE)
    @ResponseBody
    public MessageResult foursquareLogout(HttpServletRequest request, HttpServletResponse response) {

        socialConnectionService.logout(request, response);

        return new MessageResult("ok");
    }

    private String getScope(HttpServletRequest request) {

        String scope = request.getParameter("scope");
        return scope;
    }
}
