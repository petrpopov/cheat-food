package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.*;
import com.petrpopov.cheatfood.model.entity.Comment;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.service.LocationRepository;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserConnectionsService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.PageableRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
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

    @Autowired
    private UserConnectionsService userConnectionsService;

    @Autowired
    private LocationRepository locationRepository;

    @RequestMapping(value = "all", method = RequestMethod.GET)
    @ResponseBody
    public List<Location> findAllLocations(@Valid PageableRequest pageable) {

        Pageable page = new PageRequest(pageable.getPage(), pageable.getSize() );
        Page<Location> all = locationRepository.findAll(page);

        return all.getContent();
    }

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

    @RequestMapping(value = "totalcount", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult getLocationsTotalCount() {

        MessageResult res = new MessageResult();

        long count = locationService.getLocationsTotalCount();
        res.setResult(count);

        return res;
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

    @RequestMapping(value = "{locationid}/addprofile", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult addLocationToProfile(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                                   @PathVariable String locationid) {

        UserEntity userEntity = userContextHandler.currentContextUser();
        Location location = locationService.findById(locationid);

        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        userConnectionsService.restoreLocationToUserConnections(location, userEntity);

        result.setResult(location);

        return result;
    }

    @RequestMapping(value = "{locationid}/deleteprofile", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult deleteLocationFromProfile(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                        @PathVariable String locationid) {

        UserEntity userEntity = userContextHandler.currentContextUser();
        Location location = locationService.findById(locationid);

        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        userConnectionsService.removeLocationFromUserConnections(location, userEntity);

        result.setResult(location);

        return result;
    }

    @RequestMapping(value = "{locationid}/restoreprofile", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult restoreLocationToProfile(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                                   @PathVariable String locationid) {

        UserEntity userEntity = userContextHandler.currentContextUser();
        Location location = locationService.findById(locationid);

        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        userConnectionsService.restoreLocationToUserConnections(location, userEntity);

        result.setResult(location);

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

    @RequestMapping(value = "{locationid}/comments", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult comments(@PathVariable String locationid) throws CheatException {

        MessageResult result = new MessageResult();

        List<Comment> comments = locationService.getCommentsForLocation(locationid);
        result.setResult(comments);

        return result;
    }

    @RequestMapping(value = "{locationid}/comments/add", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult addComment(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                    @PathVariable String locationid,
                                    @Valid @RequestBody Comment comment) throws CheatException {

        MessageResult result = new MessageResult();

        UserEntity author = userContextHandler.currentContextUser();
        locationService.addCommentToLocation(locationid, comment, author);

        return result;
    }

    @RequestMapping(value = "{locationid}/comments/{commentid}/vote", method = RequestMethod.POST)
    @ResponseBody
    public MessageResult voteForComment(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                        @PathVariable String locationid,
                                        @PathVariable String commentid,
                                        @Valid @RequestBody Boolean value) throws CheatException {

        MessageResult result = new MessageResult();

        UserEntity author = userContextHandler.currentContextUser();
        Comment comment = locationService.voteForComment(locationid, commentid, author, value);

        result.setResult(comment);

        return result;
    }

    @RequestMapping(value = "{locationid}/comments/{commentid}/delete", method = RequestMethod.DELETE)
    @ResponseBody
    public MessageResult deleteComment(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                       @PathVariable String locationid,
                                       @PathVariable String commentid) throws CheatException {

        MessageResult result = new MessageResult();

        Comment comment = locationService.getCommentForLocation(locationid, commentid);
        if( comment == null ) {
            result.setError(true);
            result.setErrorType(ErrorType.unknown_comment);
            return result;
        }

        locationService.deleteComment(comment, locationid);

        return result;
    }
}
