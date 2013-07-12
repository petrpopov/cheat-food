package com.petrpopov.cheatfood.web;

import com.petrpopov.cheatfood.model.Type;
import com.petrpopov.cheatfood.service.TypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;


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

    @Autowired
    private TypeService typeService;


    @RequestMapping(value="types", method = RequestMethod.GET)
    @ResponseBody
    public String[] getLocationTypesList() {
        return types;
    }

    @RequestMapping(value="types1", method = RequestMethod.GET)
    @ResponseBody
    public List<Type> getLocationTypes() {
        return typeService.findAll();
    }
}
