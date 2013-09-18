package com.petrpopov.cheatfood.web.controllers;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.filters.LocationFilter;
import com.petrpopov.cheatfood.web.other.ImageService;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import java.io.IOException;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:18
 */
@Controller
public class HomeController {

    @Autowired
    private LocationService locationService;

    @Autowired
    private LocationFilter locationFilter;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private ImageService imageService;

    @RequestMapping(value = "/favicon.ico", produces = "image/png")
    @ResponseBody
    public byte[] favicon() {
        return imageService.getFavicon();
    }

    @RequestMapping({"/","/home", "/index", "/main"})
    public String showHomePage() {
        return "index";
    }

    @RequestMapping({"/manifest"})
    public String showManifestPage() {
        return "manifest";
    }

    @RequestMapping({"/help"})
    public String showHelpPage() {
        return "help";
    }

    @RequestMapping({"/restore"})
    public String showRestorePage() {
        return "restore";
    }

    @RequestMapping("/signin")
    public ModelAndView showLoginPage() {

        UserEntity entity = userContextHandler.currentContextUser();
        if( entity != null )
            return new ModelAndView(new RedirectView("index"));

        return new ModelAndView("signin");
    }

    @RequestMapping(value = "location/{locationid}")
    @ResponseBody
    public ModelAndView getLocation(@PathVariable String locationid) throws IOException {

        Location location = locationService.findById(locationid);
        if( location == null ) {
            return new ModelAndView(new RedirectView("/", true));
        }
        locationFilter.filterLocation(location);

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
