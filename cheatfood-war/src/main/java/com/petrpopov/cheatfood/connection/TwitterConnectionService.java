package com.petrpopov.cheatfood.connection;

import com.petrpopov.cheatfood.config.AppSettings;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.twitter.api.Twitter;

/**
 * User: petrpopov
 * Date: 22.07.13
 * Time: 12:59
 */
public class TwitterConnectionService extends GenericConnectionService<Twitter> {

    public TwitterConnectionService() {
        super(AppSettings.TWITTER_CALLBACK_URL, Twitter.class);
    }

    public TwitterConnectionService(ConnectionFactoryLocator registry) {
        super(registry, AppSettings.TWITTER_CALLBACK_URL, Twitter.class);
    }
}
