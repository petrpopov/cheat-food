package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.config.AppSettings;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 15.02.13
 * Time: 11:56
 */

@Component
public class FoursquareConnectionService extends GenericConnectionService<Foursquare> {

    public FoursquareConnectionService() {
        super(AppSettings.FOURSQUARE_CALLBACK_URL, Foursquare.class);
    }

    public FoursquareConnectionService(ConnectionFactoryLocator registry) {
        super(registry, AppSettings.FOURSQUARE_CALLBACK_URL, Foursquare.class);
    }
}
