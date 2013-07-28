package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.ErrorType;
import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.model.Vote;
import com.petrpopov.cheatfood.service.CookieService;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import com.petrpopov.cheatfood.web.other.MessageResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * User: petrpopov
 * Date: 24.07.13
 * Time: 22:11
 */

@Controller
@RequestMapping("/api/votes")
public class VoteWebService {

    @Autowired
    private CookieService cookieService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserContextHandler userContextHandler;

    @RequestMapping(value="add", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult voteForLocation(@CookieValue(required = true, value = "CHEATFOOD") String cookie,
                                         @RequestParam String locationId, @RequestParam Boolean value) {

        MessageResult result = new MessageResult();

        boolean valid = cookieService.isCookieValidForCurrentUser(new CookieRequest(cookie));
        if( !valid ) {
            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
            return result;
        }

        Location location = locationService.findById(locationId);
        if( location == null ) {
            result.setError(true);
            result.setErrorType(ErrorType.unknown_location);
            result.setMessage("Unknown location!");
            return result;
        }

        UserEntity entity = userContextHandler.currentContextUser();

        Vote vote = new Vote();
        vote.setUserId(entity.getId());
        vote.setValue(value);

        try {
            locationService.voteForLocation(location, vote);
        }
        catch (AccessDeniedException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.access_denied);
            result.setMessage("Access denied");
            return result;
        }
        catch (CheatException e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(e.getErrorType());
            return result;
        }
        catch (Exception e) {
            e.printStackTrace();

            result.setError(true);
            result.setErrorType(ErrorType.other);
            return result;
        }


        result.setResult("OK");

        return result;
    }
}
