package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.Type;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.ITypeService;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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

    @Autowired
    private CookieService cookieService;

    private Logger logger = Logger.getLogger(ParamsWebService.class);

    @RequestMapping(value="types", method = RequestMethod.GET)
    @ResponseBody
    public List<Type> getLocationTypes() {

        logger.info("Returning all types from database to WEB response");
        return typeService.findAll();
    }

    @RequestMapping(value = "/checkcookie", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult checkCookies(@RequestBody CookieRequest cookie, HttpServletRequest request, HttpServletResponse response) {

        MessageResult res = new MessageResult();

        try {
            cookieService.isCookieValidForCurrentUser(cookie, request, response);
        } catch (CheatException e) {

            res.setError(true);
            res.setMessage(e.getMessage());

            e.printStackTrace();

            return res;
        }

        res.setMessage("OK");

        return res;
    }
}
