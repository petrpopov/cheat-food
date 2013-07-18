package com.petrpopov.cheatfood.config;

import com.petrpopov.cheatfood.connection.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.social.connect.*;
import org.springframework.social.connect.mongo.MongoConnectionService;
import org.springframework.social.connect.mongo.MongoUsersConnectionRepository;
import org.springframework.social.connect.support.ConnectionFactoryRegistry;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.foursquare.api.Foursquare;
import org.springframework.social.foursquare.connect.FoursquareConnectionFactory;

import javax.inject.Inject;

/**
 * User: petrpopov
 * Date: 15.02.13
 * Time: 11:11
 */

@Configuration
public class SocialConfig {

    @Inject
    private Environment environment;

    @Autowired
    private MongoConnectionService mongoConnectionService;

    @Autowired
    private ConnectionSignUp connectionSignUp;

    @Autowired
    private CheatTextEncryptor textEncryptor;

    @Bean
    public ConnectionFactoryLocator connectionFactoryLocator()
    {
        ConnectionFactoryRegistry registry = new ConnectionFactoryRegistry();

        FoursquareConnectionFactory foursquareFactory = new FoursquareConnectionFactory(AppSettings.FOURSQUARE_CLIENT_ID,
                AppSettings.FOURSQUARE_CLIENT_SECRET);
        FacebookConnectionFactory facebookFactory = new FacebookConnectionFactory(AppSettings.FACEBOOK_CLIENT_ID,
                AppSettings.FACEBOOK_CLIENT_SECRET);

        registry.addConnectionFactory(foursquareFactory);
        registry.addConnectionFactory(facebookFactory);

        return registry;
    }

    @Bean
    public FoursquareConnectionFactory foursquareConnectionFactory()
    {
        FoursquareConnectionFactory factory = (FoursquareConnectionFactory) connectionFactoryLocator()
                .getConnectionFactory(Foursquare.class);
        return factory;
    }

    @Bean
    public FacebookConnectionFactory facebookConnectionFactory()
    {
        FacebookConnectionFactory factory = (FacebookConnectionFactory) connectionFactoryLocator().getConnectionFactory(Facebook.class);
        return factory;
    }

    @Bean
    public UsersConnectionRepository usersConnectionRepository()
    {
        MongoUsersConnectionRepository repo = new MongoUsersConnectionRepository(mongoConnectionService,
                connectionFactoryLocator(), textEncryptor);
        repo.setConnectionSignUp( connectionSignUp );

        return repo;
    }

    @Bean
    @Scope(value="request", proxyMode= ScopedProxyMode.INTERFACES)
    public ConnectionRepository connectionRepository()
    {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new IllegalStateException("Unable to get a ConnectionRepository: no user signed");
        }

        //this is a real mongodb _id userentity id, not a providerUserId
        String name = authentication.getName();
        ConnectionRepository repo = usersConnectionRepository().createConnectionRepository(name);
        return repo;
    }

    @Bean
    @Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
    public Foursquare foursquare()
    {
        ConnectionRepository repo = connectionRepository();
        try {
            Connection<Foursquare> connection = repo.getPrimaryConnection(Foursquare.class);
            Foursquare api = connection.getApi();
            return api;
        }
        catch (Exception e) {
            return new FoursquareDefaultBean();
        }
    }

    @Bean
    @Scope(value="request", proxyMode=ScopedProxyMode.INTERFACES)
    public Facebook facebook() {
        ConnectionRepository repo = connectionRepository();
        try {
            Connection<Facebook> connection = repo.getPrimaryConnection(Facebook.class);
            Facebook api = connection.getApi();
            return api;
        }
        catch (Exception e) {
            return new FacebookDefaultBean();
        }
    }

    @Bean
    public ProviderIdClassStorage providerIdClassStorage() {
        return new ProviderIdClassStorage();
    }

    @Bean
    public ConnectionServiceFactory connectionServiceFactory() {
        ConnectionServiceFactory serviceFactory = new ConnectionServiceFactory(providerIdClassStorage());

        serviceFactory.addConnectionService(new FacebookConnectionService(connectionFactoryLocator()));
        serviceFactory.addConnectionService(new FoursquareConnectionService(connectionFactoryLocator()));

        return serviceFactory;
    }
}
