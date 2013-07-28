package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.Rate;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.service.UserContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 28.07.13
 * Time: 12:38
 */

@Component
public class LocationRateService {

    @Autowired
    private UserContextHandler userContextHandler;

    public void setAlreadyRated(Location location) {
        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity == null )
            location.setAlreadyRated(false);

        if( this.hasLocationRatedByUser(location, userEntity) )
            location.setAlreadyRated(true);
        else
            location.setAlreadyRated(false);
    }

    public void setAlreadyRated(List<Location> list) {

        if( list == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();

        for (Location location : list) {
            if( userEntity == null ) {
                location.setAlreadyRated(false);
            }
            else {
                if( this.hasLocationRatedByUser(location, userEntity) )
                    location.setAlreadyRated(true);
                else
                    location.setAlreadyRated(false);
            }
        }
    }

    private boolean hasLocationRatedByUser(Location location, UserEntity userEntity) {
        List<Rate> rates = location.getRates();
        if( rates == null )
            return false;

        for (Rate rate : rates) {
            if( rate.getUserId().equals(userEntity.getId() ))
                return true;
        }

        return false;
    }
}