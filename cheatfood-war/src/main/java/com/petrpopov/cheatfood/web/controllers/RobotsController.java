package com.petrpopov.cheatfood.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;

/**
 * User: petrpopov
 * Date: 18.09.13
 * Time: 17:17
 */

@Controller
public class RobotsController {

    @RequestMapping(value = "/robots.txt", method = RequestMethod.GET, produces = "text/plain")
    @ResponseBody
    public String getRobots(HttpServletRequest request) {

        StringBuilder builder = new StringBuilder();

        builder.append("User-agent: *");
        builder.append("\n");
        builder.append("Disallow: /api");
        builder.append("\n");

        builder.append("\n");

        builder.append("User-agent: Yandex");
        builder.append("\n");
        builder.append("Disallow: /api");
        builder.append("\n");
        builder.append("Host: www.cheatfood.com");
        builder.append("\n");
        builder.append("Crawl-delay: 30");
        builder.append("\n");

        builder.append("\n");

        builder.append("User-agent: Googlebot");
        builder.append("\n");
        builder.append("Disallow: /api");
        builder.append("\n");
        builder.append("Crawl-delay: 30");
        builder.append("\n");

        builder.append("\n");

        builder.append("Sitemap: http://www.cheatfood.com/sitemap.xml");

        return builder.toString();
    }
}
