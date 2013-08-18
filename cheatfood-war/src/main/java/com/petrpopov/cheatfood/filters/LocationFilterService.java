package com.petrpopov.cheatfood.filters;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.UserEntity;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 18.08.13
 * Time: 21:12
 */

@Component
public class LocationFilterService {

    public void filterCreator(List<Location> list) {
        for (Location location : list) {
            filterCreator(location);
        }
    }

    public Location filterCreator(Location location) {

        UserEntity creator = location.getCreator();
        if( creator == null )
            return location;

        location.setCreator( new UserEntity(creator.getId() ) );
        return location;
    }

    public void filterVotes(List<Location> list) {
        for (Location location : list) {
            this.filterVotes(location);
        }
    }

    public Location filterVotes(Location location) {
        location.setVotes(null);
        return location;
    }

    public void filterRates(List<Location> list) {
        for (Location location : list) {
            this.filterRates(location);
        }
    }

    public Location filterRates(Location location) {
        location.setRates(null);
        return location;
    }
}
