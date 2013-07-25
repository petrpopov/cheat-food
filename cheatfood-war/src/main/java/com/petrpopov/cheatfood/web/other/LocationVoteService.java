package com.petrpopov.cheatfood.web.other;

import com.petrpopov.cheatfood.model.Location;
import com.petrpopov.cheatfood.model.UserEntity;
import com.petrpopov.cheatfood.model.Vote;
import com.petrpopov.cheatfood.service.UserContextHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * User: petrpopov
 * Date: 25.07.13
 * Time: 20:52
 */

@Component
public class LocationVoteService {

    @Autowired
    private UserContextHandler userContextHandler;

    public void setAlreadyVoted(Location location) {
        UserEntity userEntity = userContextHandler.currentContextUser();
        if( userEntity == null )
            location.setAlreadyVoted(false);

        if( this.hasLocationVotedByUser(location, userEntity) )
            location.setAlreadyVoted(true);
        else
            location.setAlreadyVoted(false);
    }

    public void setAlreadyVoted(List<Location> list) {

        if( list == null )
            return;

        UserEntity userEntity = userContextHandler.currentContextUser();

        for (Location location : list) {
            if( userEntity == null ) {
                location.setAlreadyVoted(false);
            }
            else {
                if( this.hasLocationVotedByUser(location, userEntity) )
                    location.setAlreadyVoted(true);
                else
                    location.setAlreadyVoted(false);
            }
        }
    }

    private boolean hasLocationVotedByUser(Location location, UserEntity userEntity) {
        List<Vote> votes = location.getVotes();
        if( votes == null )
            return false;

        for (Vote vote : votes) {
            if( vote.getUserId().equals(userEntity.getId() ))
                return true;
        }

        return false;
    }
}
