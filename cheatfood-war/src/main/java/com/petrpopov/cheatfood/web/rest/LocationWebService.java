package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.service.ILocationService;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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

    @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value="location", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public Location createLocation(@Valid @RequestBody Location location)
    {
        Location result = locationService.createOrSave(location);

        return result;
    }

 //   @PreAuthorize("hasRole('ROLE_USER')")
    @RequestMapping(value = "location/{locationid}", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult deleteLocation(@PathVariable String locationid) {

        Location location = locationService.findById(locationid);
        locationService.deleteLocation(location);

        MessageResult res = new MessageResult();
        return res;
    }
}
