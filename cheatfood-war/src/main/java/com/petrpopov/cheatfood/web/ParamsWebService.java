package com.petrpopov.cheatfood.web;

import com.petrpopov.cheatfood.model.Type;
import com.petrpopov.cheatfood.service.ITypeService;
import org.apache.log4j.Logger;
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

    @Autowired
    private ITypeService typeService;

    private Logger logger = Logger.getLogger(ParamsWebService.class);

    @RequestMapping(value="types", method = RequestMethod.GET)
    @ResponseBody
    public List<Type> getLocationTypes() {

        logger.info("Returning all types from database to WEB response");
        return typeService.findAll();
    }
}
