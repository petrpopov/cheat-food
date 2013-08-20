package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.Rate;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.service.UserContextHandler;
import org.joda.time.DateTime;
import org.joda.time.Days;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * User: petrpopov
 * Date: 28.07.13
 * Time: 12:38
 */

@Component
public class LocationRateFilter {

    @Autowired
    private UserContextHandler userContextHandler;

    @Value("#{properties.rate_days_delay}")
    private int rateDaysDelay;

    public void setAlreadyRated(Location location) {

        if( location == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity == null )
            location.setAlreadyRated(false);

        boolean canVote = canUserRateForLocation(location, userEntity);
        location.setAlreadyRated(!canVote);
    }

    public void setAlreadyRated(List<Location> list) {

        if( list == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();

        for (Location location : list) {

            if( location == null )
                continue;

            if( userEntity == null ) {
                location.setAlreadyRated(false);
            }
            else {
                boolean canVote = canUserRateForLocation(location, userEntity);
                location.setAlreadyRated(!canVote);
            }
        }
    }

    public boolean canUserRateForLocation(Location location, UserEntity userEntity) {

        Rate lastRate = this.getLastRate(location, userEntity);
        if( lastRate == null )
            return true;

        Date date = lastRate.getDate();
        if( date == null )
            return true;

        DateTime rateDate = new DateTime(date);
        DateTime currentDate = new DateTime(new Date());

        Days days = Days.daysBetween(currentDate, rateDate);
        int diff = days.getDays();
        if( diff >= rateDaysDelay )
            return true;

        return false;
    }

    private Rate getLastRate(Location location, UserEntity userEntity) {

        if( location == null || userEntity == null )
            return null;

        List<Rate> rates = location.getRates();
        if( rates == null )
            return null;

        List<Rate> list = new ArrayList<Rate>();
        for (Rate rate : rates) {
            if( rate.getUserId().equals(userEntity.getId())) {
                list.add(rate);
            }
        }

        if(list.size() <= 0 )
            return null;

        Rate last = list.get(0);
        for (Rate rate : list) {

            if( rate == null )
                continue;

            Date date = rate.getDate();
            if( date == null )
                continue;

            Date lastDate = last.getDate();
            if( lastDate == null )
                continue;

            DateTime rateDateTime = new DateTime(date);
            DateTime lastDateTime = new DateTime(lastDate);

            if( lastDateTime.isAfter(rateDateTime) ) {
                last = rate;
            }
        }

        return last;
    }
}
