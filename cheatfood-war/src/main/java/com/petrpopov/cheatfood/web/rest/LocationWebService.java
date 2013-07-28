package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.model.*;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.service.impl.LocationService;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.LocationVoteService;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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
public class LocationWebService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private LocationVoteService locationVoteService;

    @Autowired
    private CookieService cookieService;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocations() throws Exception {

        List<Location> list = locationService.findAll();
        locationVoteService.setAlreadyVoted(list);
        return list;
    }

    @RequestMapping(value = "inbounds", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocationsInDifferenceBetweenBounds(@Valid GeoJSONPointBoundsDiff diff, String typeId) {

        GeoPointBounds current = diff.getCurrent();
        GeoPointBounds previous = diff.getPrevious();

        List<Location> list = locationService.findAllInDifference(current, previous, typeId);
        locationVoteService.setAlreadyVoted(list);
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


    @RequestMapping(value="add", method = RequestMethod.POST, headers = "Accept=application/json")
    @ResponseBody
    public MessageResult createLocation(@Valid @RequestBody Location location,
                                        @CookieValue(required = true, value = "CHEATFOOD") String cookie)
    {
        Location loc = null;
        MessageResult result = new MessageResult();

        boolean valid = cookieService.isCookieValidForCurrentUser(new CookieRequest(cookie));
        if( !valid ) {
            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
            return result;
        }

        try {
            UserEntity userEntity = userContextHandler.currentContextUser();
            loc = locationService.createOrSave(location, userEntity);
        }
        catch (AccessDeniedException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
        }
        catch (Exception e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.other);
            result.setMessage("Unknown error");
        }

        locationVoteService.setAlreadyVoted(loc);

        if( loc != null )
            result.setResult(loc);

        return result;
    }

    @RequestMapping(value = "{locationid}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult deleteLocation(@CookieValue(required = true, value = "CHEATFOOD") String cookie, @PathVariable String locationid) {

        MessageResult result = new MessageResult();

        boolean valid = cookieService.isCookieValidForCurrentUser(new CookieRequest(cookie));
        if( !valid ) {
            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
            return result;
        }

        Location location = locationService.findById(locationid);
        if( location == null ) {
            result.setError(true);
            result.setErrorType(ErrorType.unknown_location);
            result.setMessage("There is no such location!");

            return result;
        }

        try {
            locationService.deleteLocation(location);
        }
        catch (AccessDeniedException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
        }
        catch (Exception e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.other);
            result.setMessage("Unknown error");
        }


        return result;
    }
}
