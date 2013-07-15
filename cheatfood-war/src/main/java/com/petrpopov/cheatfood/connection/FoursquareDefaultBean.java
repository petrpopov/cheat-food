package com.petrpopov.cheatfood.connection;

import org.springframework.social.foursquare.api.*;

/**
 * User: petrpopov
 * Date: 19.02.13
 * Time: 22:08
 */
public class FoursquareDefaultBean implements Foursquare {

    @Override
    public UserOperations userOperations() {
        return null;
    }

    @Override
    public VenueOperations venueOperations() {
        return null;
    }

    @Override
    public CheckinOperations checkinOperations() {
        return null;
    }

    @Override
    public TipOperations tipOperations() {
        return null;
    }

    @Override
    public PhotoOperations photoOperations() {
        return null;
    }

    @Override
    public SettingOperations settingOperations() {
        return null;
    }

    @Override
    public SpecialOperations specialOperations() {
        return null;
    }
}
