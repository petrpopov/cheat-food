package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.web.other.XmlUrl;
import com.petrpopov.cheatfood.web.other.XmlUrlSet;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * User: petrpopov
 * Date: 18.09.13
 * Time: 17:07
 */

@Controller
public class SitemapController {

    private String siteUrl = "http://www.cheatfood.com";

    @RequestMapping(value = "/sitemap.xml", method = RequestMethod.GET)
    @ResponseBody
    public XmlUrlSet main() {

        XmlUrlSet xmlUrlSet = new XmlUrlSet();
        create(xmlUrlSet, "", XmlUrl.Priority.HIGH);
        create(xmlUrlSet, "/index", XmlUrl.Priority.HIGH);
        create(xmlUrlSet, "/main", XmlUrl.Priority.MEDIUM);
        create(xmlUrlSet, "/home", XmlUrl.Priority.MEDIUM);
        create(xmlUrlSet, "/manifest", XmlUrl.Priority.MEDIUM);
        create(xmlUrlSet, "/help", XmlUrl.Priority.MEDIUM);


        return xmlUrlSet;
    }

    private void create(XmlUrlSet xmlUrlSet, String link, XmlUrl.Priority priority) {
        xmlUrlSet.addUrl(new XmlUrl(siteUrl + link, priority));
    }
}
