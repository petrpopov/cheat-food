package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 18.08.13
 * Time: 21:12
 */

@Component
public class LocationFieldFilter {

    @Autowired
    private UserEntityFilter userEntityFilter;

    public void filterCreator(List<Location> list) {
        for (Location location : list) {
            filterCreator(location);
        }
    }

    public Location filterCreator(Location location) {

        if( location == null )
            return location;

        UserEntity creator = location.getCreator();
        location.setCreator( getUserEntity(creator) );

        return location;
    }

    public void filterVotes(List<Location> list) {
        for (Location location : list) {
            this.filterVotes(location);
        }
    }

    public Location filterVotes(Location location) {

        if( location == null )
            return location;

        location.setVotes(null);
        return location;
    }

    public void filterRates(List<Location> list) {
        for (Location location : list) {
            this.filterRates(location);
        }
    }

    public Location filterRates(Location location) {

        if( location == null )
            return location;

        location.setRates(null);
        return location;
    }

    private UserEntity getUserEntity(UserEntity creator) {

        String id = (creator == null ? "-1" : creator.getId());
        UserEntity entity = new UserEntity(id);

        String firstName = (creator == null ? "" : creator.getFirstName() );
        entity.setFirstName(firstName);

        String lastName = (creator == null ? "" : creator.getLastName() );
        entity.setLastName(lastName);
        entity.setPublicName(userEntityFilter.getPublicName(creator));

        return entity;
    }


}
