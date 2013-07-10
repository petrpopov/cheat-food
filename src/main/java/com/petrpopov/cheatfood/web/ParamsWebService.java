package com.petrpopov.cheatfood.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;


/**
 * User: petrpopov
 * Date: 09.07.13
 * Time: 13:24
 */

@Controller
@RequestMapping("/api")
public class ParamsWebService {

    private String[] types = new String[]{
            "Шашлычная", "Чебуречная", "Шаверменная", "Чайхона",
            "Блинная", "Бутербродная", "Рюмочная",
            "Булочная", "Столовая", "Кафе", "Кулинария", "Другое"};

    @RequestMapping(value="types", method = RequestMethod.GET)
    @ResponseBody
    public String[] getLocationTypesList() {
        return types;
    }
}
