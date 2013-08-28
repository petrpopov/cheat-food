package com.petrpopov.cheatfood.connection;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.GenericTypeResolver;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 18:27
 */


public class ConnectionServiceFactory {

    @Autowired
    private UsersConnectionRepository usersConnectionRepository;

    private Map<String, ConnectionService<?>> services = new HashMap<String, ConnectionService<?>>();
    private ProviderIdClassStorage providerIdClassStorage;

    public ConnectionServiceFactory(ProviderIdClassStorage providerIdClassStorage) {
        this.providerIdClassStorage = providerIdClassStorage;
    }

    public void addConnectionService(ConnectionService<?> service) {

        String providerId = service.getProviderId();
        services.put(providerId, service);

        Class<?> apiType = GenericTypeResolver.resolveTypeArgument(service.getClass(), GenericConnectionService.class);
        providerIdClassStorage.addClass(apiType, service.getProviderId());
    }

    public ConnectionService<?> getConnectionService(String providerId) {
        if( services.containsKey(providerId) ) {
            return services.get(providerId);
        }
        else
            return null;
    }

    public ConnectionService<?> getConnectionService(Class<?> apiClass) {
        if( !providerIdClassStorage.contains(apiClass) )
            return null;

        String providerId = providerIdClassStorage.getProviderIdByClass(apiClass);
        if( providerId != null ) {
            return getConnectionService(providerId);
        }

        return null;
    }

    public void removeConnectionsForUser(String id, Class<?> apiClass) {

        ConnectionRepository repo = usersConnectionRepository.createConnectionRepository(id);
        List list = repo.findConnections(apiClass);

        if( list == null )
            return;
        if( list.isEmpty() )
            return;

        String providerId = providerIdClassStorage.getProviderIdByClass(apiClass);

        if( providerId != null )
            repo.removeConnections(providerId);
    }
}
