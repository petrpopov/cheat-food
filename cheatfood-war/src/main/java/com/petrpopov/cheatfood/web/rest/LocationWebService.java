package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.filters.LocationFilterService;
import com.petrpopov.cheatfood.filters.LocationRateService;
import com.petrpopov.cheatfood.filters.LocationVoteService;
import com.petrpopov.cheatfood.model.*;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
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
    private LocationRateService locationRateService;

    @Autowired
    private LocationFilterService locationFilterService;

    @Autowired
    private CookieService cookieService;


    @RequestMapping(method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocations() throws Exception {

        List<Location> list = locationService.findAll();

        locationVoteService.setAlreadyVoted(list);
        locationRateService.setAlreadyRated(list);
        locationFilterService.filterCreator(list);
        locationFilterService.filterRates(list);
        locationFilterService.filterVotes(list);

        return list;
    }

    @RequestMapping(value = "inbounds", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> getAllLocationsInDifferenceBetweenBounds(@Valid GeoJSONPointBoundsDiff diff, String typeId) {

        GeoPointBounds current = diff.getCurrent();
        GeoPointBounds previous = diff.getPrevious();

        List<Location> list = locationService.findAllInDifference(current, previous, typeId);

        locationVoteService.setAlreadyVoted(list);
        locationRateService.setAlreadyRated(list);
        locationFilterService.filterCreator(list);
        locationFilterService.filterRates(list);
        locationFilterService.filterVotes(list);

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
        catch (CheatException e) {
            result.setError(true);
            result.setErrorType(e.getErrorType());
            result.setMessage("Overpriced!");
            return result;
        }
        catch (AccessDeniedException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.other);
            result.setMessage("Unknown error");
            return result;
        }

        locationVoteService.setAlreadyVoted(loc);
        locationRateService.setAlreadyRated(loc);
        locationFilterService.filterCreator(loc);
        locationFilterService.filterRates(loc);
        locationFilterService.filterVotes(loc);

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

    @RequestMapping(value = "{locationid}/hide", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult hideLocation(@CookieValue(required = true, value = "CHEATFOOD") String cookie, @PathVariable String locationid) {

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
            locationService.hideLocation(location);
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
