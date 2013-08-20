package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Location;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 21.08.13
 * Time: 1:27
 */

@Component
public class LocationFilter {

    @Autowired
    private LocationFieldFilter locationFieldFilter;

    @Autowired
    private LocationRateFilter locationRateFilter;

    @Autowired
    private LocationVoteFilter locationVoteFilter;

    public List<Location> filterLocations(List<Location> list) {

        if( list == null )
            return null;

        for (Location location : list) {
            filterLocation(location);
        }

        return list;
    }

    public Location filterLocation(Location location) {

        locationVoteFilter.setAlreadyVoted(location);
        locationRateFilter.setAlreadyRated(location);
        locationFieldFilter.filterCreator(location);
        locationFieldFilter.filterRates(location);
        locationFieldFilter.filterVotes(location);

        return location;
    }
}
