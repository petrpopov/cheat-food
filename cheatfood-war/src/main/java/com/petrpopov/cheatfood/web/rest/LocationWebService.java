package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.ILocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.ErrorType;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
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

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private CookieService cookieService;


    @RequestMapping(value="locations", method = RequestMethod.GET)
    public @ResponseBody
    List<Location> getAllCheckins(HttpServletRequest request) throws Exception {

        List<Location> list = locationService.findAll();
        return list;
    }


    @RequestMapping(value="location", method = RequestMethod.POST, headers = "Accept=application/json")
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

        if( loc != null )
            result.setResult(loc);

        return result;
    }

    @RequestMapping(value = "location/{locationid}", method = RequestMethod.DELETE)
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
