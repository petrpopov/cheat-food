package com.petrpopov.cheatfood.web.aspects;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.LogAction;
import com.petrpopov.cheatfood.model.entity.LogEntity;
import com.petrpopov.cheatfood.service.LocationService;
import com.petrpopov.cheatfood.service.LogService;
import com.petrpopov.cheatfood.service.UserContextHandler;
import com.petrpopov.cheatfood.web.filters.LocationFilter;
import org.apache.commons.lang3.SerializationUtils;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * User: petrpopov
 * Date: 27.08.13
 * Time: 21:33
 */

@Component
@Aspect
public class WebLogControllerAspect {

    @Autowired
    private LogService logService;

    @Autowired
    private LocationService locationService;

    @Autowired
    private UserContextHandler userContextHandler;

    @Autowired
    private LocationFilter locationFilter;

    @Before("execution(* com.petrpopov.cheatfood.web.rest.LocationWebService.createLocation(..)) && args(location,..)")
    public void logCreateOrEdit(Location location) {

        String id = null;
        if( location.getId() != null ) {
            if( !location.getId().isEmpty() ) {
                id = location.getId();
            }
        }

        LogAction action;
        Location before = null;
        if( id != null ) {
            before = locationService.findById(id);
            action = LogAction.edit;
        }
        else {
            action = LogAction.add;
        }


        Location after = SerializationUtils.clone(location);
        LogEntity log = getLogEntity(before, after, action);

        logService.save(log);
    }

    @Before("execution(* com.petrpopov.cheatfood.web.rest.LocationWebService.deleteLocation(..)) && args(..,locationid)")
    public void logDeleteLocation(String locationid) {

        Location before = locationService.findById(locationid);

        LogEntity log = getLogEntity(before, null, LogAction.delete);

        logService.save(log);
    }

    @Before("execution(* com.petrpopov.cheatfood.web.rest.LocationWebService.hideLocation(..)) && args(..,locationid)")
    public void logHideLocation(String locationid) {

        Location before = locationService.findById(locationid);
        Location after = SerializationUtils.clone(before);

        LogEntity log = getLogEntity(before, after, LogAction.hide);

        logService.save(log);
    }

    private LogEntity getLogEntity(Location before, Location after, LogAction action) {

        LogEntity log = new LogEntity();

        locationFilter.filterLocation(before);
        log.setBefore(before);

        locationFilter.filterLocation(after);
        log.setAfter(after);

        log.setAction(action);
        log.setDateTime(new Date());
        log.setOwner( userContextHandler.currentContextUser() );

        return log;
    }
}
