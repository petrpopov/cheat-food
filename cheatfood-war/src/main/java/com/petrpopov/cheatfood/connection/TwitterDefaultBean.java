package com.petrpopov.cheatfood.connection;

import org.springframework.social.twitter.api.*;
import org.springframework.web.client.RestOperations;

/**
 * User: petrpopov
 * Date: 22.07.13
 * Time: 13:00
 */
public class TwitterDefaultBean implements Twitter {
    @Override
    public BlockOperations blockOperations() {
        return null;
    }

    @Override
    public DirectMessageOperations directMessageOperations() {
        return null;
    }

    @Override
    public FriendOperations friendOperations() {
        return null;
    }

    @Override
    public GeoOperations geoOperations() {
        return null;
    }

    @Override
    public ListOperations listOperations() {
        return null;
    }

    @Override
    public SearchOperations searchOperations() {
        return null;
    }

    @Override
    public TimelineOperations timelineOperations() {
        return null;
    }

    @Override
    public UserOperations userOperations() {
        return null;
    }

    @Override
    public RestOperations restOperations() {
        return null;
    }

    @Override
    public boolean isAuthorized() {
        return false;
    }
}
