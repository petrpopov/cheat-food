package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.Params;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.TypeService;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * User: petrpopov
 * Date: 09.07.13
 * Time: 13:24
 */

@Controller
@RequestMapping("/api")
public class ParamsWebService {

    @Autowired
    private TypeService typeService;

    @Autowired
    private CookieService cookieService;

    private Logger logger = Logger.getLogger(ParamsWebService.class);

    @Value("#{properties.max_price}")
    private Double maxPrice;

    @Value("#{properties.recommended_price}")
    private Double recommendedPrice;

    @RequestMapping(value="params", method = RequestMethod.GET)
    @ResponseBody
    public Params getParams() {

        Params params = new Params();

        logger.info("Returning all types from database to WEB response");
        params.setTypes(typeService.findAll());
        params.setMaxPrice(maxPrice);
        params.setRecommendedPrice(recommendedPrice);

        return params;
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
