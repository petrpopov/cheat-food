package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.service.UserConnectionsService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 02.09.13
 * Time: 19:44
 */

@Component
public class LocationFavFilter {

    @Autowired
    private UserConnectionsService userConnectionsService;

    @Autowired
    private UserContextHandler userContextHandler;

    public Location filterFavourites(Location location) {

        if( location == null )
            return null;

        UserEntity entity = userContextHandler.currentContextUser();
        if( entity == null ) {
            location.setInFavourites(false);
            return location;
        }

        boolean contains = userConnectionsService.contains(entity, location);
        boolean removed = userConnectionsService.containsRemoved(entity, location);

        if( removed == true ) {
            location.setInFavourites(false);
            return location;
        }

        location.setInFavourites(contains);
        return location;
    }
}
