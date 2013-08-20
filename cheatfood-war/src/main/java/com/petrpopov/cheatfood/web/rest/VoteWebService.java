package com.petrpopov.cheatfood.web.rest;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.data.MessageResult;
import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.Rate;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.Vote;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.other.CookieRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

/**
 * User: petrpopov
 * Date: 24.07.13
 * Time: 22:11
 */

@Controller
@RequestMapping("/api")
public class VoteWebService extends BaseWebService {

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserContextHandler userContextHandler;

    @RequestMapping(value="votes/add", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult voteForLocation(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                         @RequestParam String locationId, @RequestParam Boolean value) throws CheatException {

        Location location = locationService.findById(locationId);
        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        UserEntity entity = userContextHandler.currentContextUser();

        Vote vote = new Vote();
        vote.setUserId(entity.getId());
        vote.setValue(value);

        location = locationService.voteForLocation(location, vote);
        result.setResult(location);

        return result;
    }

    @RequestMapping(value="rates/add", method = RequestMethod.GET)
    @ResponseBody
    public MessageResult rateForLocation(@CookieValue(required = true, value = "CHEATFOOD") CookieRequest cookie,
                                         @RequestParam String locationId, @RequestParam Integer value) throws CheatException {

        Location location = locationService.findById(locationId);
        MessageResult result = checkIfLocationExists(location);
        if(result.getError().equals(Boolean.TRUE))
            return result;

        UserEntity entity = userContextHandler.currentContextUser();

        Rate rate = new Rate();
        rate.setUserId(entity.getId());
        rate.setValue(value);

        location = locationService.rateForLocation(location, rate);
        result.setResult(location);

        return result;
    }
}
