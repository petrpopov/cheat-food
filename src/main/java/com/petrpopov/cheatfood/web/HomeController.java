package com.petrpopov.cheatfood.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Map;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:18
 */
@Controller
public class HomeController {

    @RequestMapping({"/","/home", "/index", "/main"})
    public String showHomePage(@CookieValue(required = false, value = "guid") String cookie,
                               Map<String, Object> model)
    {
        return "index";
    }
}
