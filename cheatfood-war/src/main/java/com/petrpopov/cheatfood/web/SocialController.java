package com.petrpopov.cheatfood.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 11:44
 */

@Controller
public class SocialController {

    @Autowired
    private SocialConnectionService socialConnectionService;

    @RequestMapping(value="connect/foursquare", method= RequestMethod.POST)
    public RedirectView foursquareConnect() {

        String url = socialConnectionService.getAuthorizeUrl(Foursquare.class);

        return new RedirectView(url);
    }

    @RequestMapping(value="connect/facebook", method= RequestMethod.POST)
    public RedirectView facebookConnect() {

        String url = socialConnectionService.getAuthorizeUrl(Facebook.class);

        return new RedirectView(url);
    }

    @RequestMapping(value="connect/foursquare", method=RequestMethod.GET, params="code")
    public RedirectView foursquareCallback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response)
    {
        socialConnectionService.apiCallback(code, Foursquare.class, request, response);

        return new RedirectView("/index", true);
    }

    @RequestMapping(value="connect/facebook", method=RequestMethod.GET, params="code")
    public RedirectView facebookCallback(@RequestParam("code") String code, HttpServletRequest request, HttpServletResponse response)
    {
        socialConnectionService.apiCallback(code, Facebook.class, request, response);

        return new RedirectView("/index", true);
    }
}
