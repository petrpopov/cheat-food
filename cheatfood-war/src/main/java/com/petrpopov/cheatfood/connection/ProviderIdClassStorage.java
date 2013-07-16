package com.petrpopov.cheatfood.connection;

import org.springframework.social.connect.Connection;

import java.util.HashMap;
import java.util.Map;

/**
 * User: petrpopov
 * Date: 15.07.13
 * Time: 19:06
 */

public class ProviderIdClassStorage {

    private Map<Class<?>, String> apiIndex = new HashMap<Class<?>, String>();

    public boolean contains(Class<?> apiClass) {
        return apiIndex.containsKey(apiClass);
    }

    public void addClass(Class<?> apiClass, String providerId) {
        apiIndex.put(apiClass, providerId);
    }

    public String getProviderIdByClass(Class<?> apiClass) {

        if( apiIndex.containsKey(apiClass) ) {
            return apiIndex.get(apiClass);
        }

        return null;
    }

    public Class<?> getProviderClassById(String providerId) {

        if( apiIndex.containsValue(providerId) ) {
            for (Map.Entry<Class<?>, String> entry : apiIndex.entrySet()) {
                if( entry.getValue().equals(providerId) ) {
                    return entry.getKey();
                }
            }
        }

        return null;
    }

    public Class<?> getProviderClassByConnection(Connection<?> connection) {

        String providerId = connection.getKey().getProviderId();
        return this.getProviderClassById(providerId);
    }

}
