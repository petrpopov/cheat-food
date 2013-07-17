package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.service.ILocationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:53
 */

@Controller
@RequestMapping("/api")
public class LocationWebService {

    @Autowired
    private ILocationService locationService;


    @RequestMapping(value="locations", method = RequestMethod.GET)
    public @ResponseBody
    List<Location> getAllCheckins(HttpServletRequest request) throws Exception {

        List<Location> list = locationService.findAll();
        return list;
    }

    @RequestMapping(value="location", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public Location createLocation(@Valid @RequestBody Location location)
    {
        Location result = locationService.createOrSaveLocation(location);

        return result;
    }

    @RequestMapping(value = "location/{locationid}", method = RequestMethod.DELETE)
    @ResponseBody
    public String deleteLocation(@PathVariable String locationid, HttpServletResponse response) {
        locationService.deleteLocation(locationid);
        return "OK";
    }
}
