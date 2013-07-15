package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.config.AppSettings;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.stereotype.Component;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 14:40
 */

@Component
public class FacebookConnectionService extends GenericConnectionService<Facebook> {

    public FacebookConnectionService() {
        super(AppSettings.FACEBOOK_CALLBACK_URL, Facebook.class);
    }

    public FacebookConnectionService(ConnectionFactoryLocator registry) {
        super(registry, AppSettings.FACEBOOK_CALLBACK_URL, Facebook.class);
    }
}
