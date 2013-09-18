package com.petrpopov.cheatfood.web.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

/**
 * User: petrpopov
 * Date: 19.08.13
 * Time: 12:52
 */


//http://stackoverflow.com/questions/1196569/custom-404-using-spring-dispatcherservlet
@Controller
public class HTTPErrorController {

    @RequestMapping(value="/errors/404")
    public ModelAndView handle404() {

        RedirectView rv = new RedirectView("error");
        rv.setStatusCode(HttpStatus.MOVED_PERMANENTLY);

        ModelAndView mv = new ModelAndView(rv);
        return mv;
    }

    @RequestMapping(value="/error")
    public String error() {
        return "error";
    }
}
