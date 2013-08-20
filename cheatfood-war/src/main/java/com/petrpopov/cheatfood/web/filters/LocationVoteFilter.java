package com.petrpopov.cheatfood.web.filters;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import com.petrpopov.cheatfood.model.entity.Vote;
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
 * Date: 25.07.13
 * Time: 20:52
 */

@Component
public class LocationVoteFilter {

    @Autowired
    private UserContextHandler userContextHandler;

    @Value("#{properties.vote_days_delay}")
    private int voteDaysDelay;

    public void setAlreadyVoted(Location location) {

        if( location == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity == null )
            location.setAlreadyVoted(false);

        boolean canVote = canUserVoteForLocation(location, userEntity);
        location.setAlreadyVoted(!canVote);
    }

    public void setAlreadyVoted(List<Location> list) {

        if( list == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();

        for (Location location : list) {

            if( location == null )
                continue;

            if( userEntity == null ) {
                location.setAlreadyVoted(false);
            }
            else {
                boolean canVote = canUserVoteForLocation(location, userEntity);
                location.setAlreadyVoted(!canVote);
            }
        }
    }

    public boolean canUserVoteForLocation(Location location, UserEntity userEntity) {

        Vote lastVote = this.getLastVote(location, userEntity);
        if( lastVote == null )
            return true;

        Date date = lastVote.getDate();
        if( date == null )
            return true;

        DateTime voteDate = new DateTime(date);
        DateTime currentDate = new DateTime(new Date());

        Days days = Days.daysBetween(currentDate, voteDate);
        int diff = days.getDays();
        if( diff >= voteDaysDelay )
            return true;

        return false;
    }

    private Vote getLastVote(Location location, UserEntity userEntity) {

        if( location == null || userEntity == null )
            return null;

        List<Vote> votes = location.getVotes();
        if( votes == null )
            return null;

        List<Vote> list = new ArrayList<Vote>();
        for (Vote vote : votes) {
            if( vote.getUserId().equals(userEntity.getId())) {
                list.add(vote);
            }
        }

        if(list.size() <= 0 )
            return null;

        Vote last = list.get(0);
        for (Vote vote : list) {

            if( vote == null )
                continue;

            Date date = vote.getDate();
            if( date == null )
                continue;

            Date lastDate = last.getDate();
            if( lastDate == null )
                continue;

            DateTime voteDateTime = new DateTime(date);
            DateTime lastDateTime = new DateTime(lastDate);

            if( lastDateTime.isAfter(voteDateTime) ) {
                last = vote;
            }
        }

        return last;
    }
}
