package com.petrpopov.cheatfood.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * User: petrpopov
 * Date: 19.08.13
 * Time: 12:52
 */


//http://stackoverflow.com/questions/1196569/custom-404-using-spring-dispatcherservlet
@Controller
public class HTTPErrorController {

    @RequestMapping(value="/errors/404")
    public String handle404() {
        return "error";
    }
}
