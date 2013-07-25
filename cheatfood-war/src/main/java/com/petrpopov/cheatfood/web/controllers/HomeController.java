package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.service.ILocationService;
import com.petrpopov.cheatfood.web.other.LocationVoteService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.io.IOException;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:18
 */
@Controller
public class HomeController {

    @Autowired
    private ILocationService locationService;

    @Autowired
    private LocationVoteService locationVoteService;

    @Autowired
    private ObjectMapper objectMapper;

    @RequestMapping({"/","/home", "/index", "/main"})
    public String showHomePage()
    {
        return "index";
    }

    @RequestMapping(value = "location/{locationid}")
    @ResponseBody
    public ModelAndView getLocation(@PathVariable String locationid) throws IOException {

        Location location = locationService.findById(locationid);
        locationVoteService.setAlreadyVoted(location);

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("location", objectMapper.writeValueAsString(location));
        return modelAndView;
    }

    @RequestMapping(value = "login")
    public ModelAndView login() {

        ModelAndView modelAndView = new ModelAndView("index");
        modelAndView.addObject("login", "needLogin");
        return modelAndView;
    }
}
