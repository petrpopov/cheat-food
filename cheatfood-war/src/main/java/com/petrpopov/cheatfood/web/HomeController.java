package com.petrpopov.cheatfood.web;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.service.ILocationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;
import java.util.Map;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:18
 */
@Controller
public class HomeController {

    @Autowired
    private ILocationService locationService;

    ObjectMapper mapper = new ObjectMapper();

    @RequestMapping({"/","/home", "/index", "/main"})
    public String showHomePage(@CookieValue(required = false, value = "guid") String cookie,
                               Map<String, Object> model)
    {
        return "index";
    }

    @RequestMapping(value = "location/{locationid}")
    @ResponseBody
    public ModelAndView getLocation(@PathVariable String locationid) throws IOException {

        Location location = locationService.findById(locationid);

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("location", mapper.writeValueAsString(location));
        return modelAndView;
    }
}
