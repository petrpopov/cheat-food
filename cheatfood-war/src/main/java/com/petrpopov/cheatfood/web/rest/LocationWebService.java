package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.GeoJSONPointBoundsDiff;
import com.petrpopov.cheatfood.model.data.GeoPointBounds;
import com.petrpopov.cheatfood.model.data.LocationsInfo;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:53
 */

@Controller
@RequestMapping("/api/locations")
public class LocationWebService extends BaseWebService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserContextHandler userContextHandler;



    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocations() throws Exception {

        List<Location> list = locationService.findAll();

        return list;
    }

    @RequestMapping(value = "userconnected", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getUserConnectedLocations() {

        UserEntity user = userContextHandler.currentContextUser();
        if( user == null )
            return null;

        List<Location> res = locationService.getUserConnectedLocations(user);
        return res;
    }

    @RequestMapping(value = "inbounds", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocationsInDifferenceBetweenBounds(@Valid GeoJSONPointBoundsDiff diff, String typeId) {

        GeoPointBounds current = diff.getCurrent();
        GeoPointBounds previous = diff.getPrevious();

        List<Location> list = locationService.findAllInDifference(current, previous, typeId);

        return list;
    }

    @RequestMapping(value = "count", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult getLocationsCountInBound(@Valid GeoPointBounds bounds) {

        MessageResult res = new MessageResult();

        long count = locationService.getLocationsCountInBound(bounds);
        res.setResult(count);

        return res;
    }

    @RequestMapping(value = "locationsinfo", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult getLocationsCountInfo(@Valid GeoPointBounds bounds) {

        MessageResult res = new MessageResult();

        LocationsInfo info = new LocationsInfo();

        long totalCount = locationService.getLocationsTotalCount();
        info.setTotalCount(totalCount);

        long regionCount = locationService.getLocationsCountInBound(bounds);
        info.setRegionCount(regionCount);

        long newCount = locationService.getLocationsNewCount();
        info.setNewCount(newCount);

        res.setResult(info);

        return res;
    }


    @RequestMapping(value="add", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult createLocation(@Valid @RequestBody Location location,
                                        @CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie) throws CheatException {

        UserEntity userEntity = userContextHandler.currentContextUser();
        Location loc = locationService.createOrSave(location, userEntity);

        MessageResult result = new MessageResult();
        result.setResult(loc);

        return result;
    }

    @RequestMapping(value = "{locationId}", method = RequestMethod.GET)
    @ResponseBody
    public Location getLocation(@PathVariable String locationId) {

        Location location = locationService.findById(locationId);

        return location;
    }

    @RequestMapping(value = "{locationid}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult deleteLocation(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                        @PathVariable String locationid) {

        Location location = locationService.findById(locationid);
        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        locationService.deleteLocation(location);
        return result;
    }

    @RequestMapping(value = "{locationid}/hide", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult hideLocation(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                      @PathVariable String locationid) {

        Location location = locationService.findById(locationid);
        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        locationService.hideLocation(location);
        return result;
    }
}
