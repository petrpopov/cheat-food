package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserConnections;
import com.petrpopov.cheatfood.model.entity.UserEntity;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 30.08.13
 * Time: 18:13
 */

@Component
public class UserConnectionsService extends GenericService<UserConnections> {

    public UserConnectionsService() {
        super(UserConnections.class);
        logger = Logger.getLogger(UserConnectionsService.class);
    }

    @Cacheable(value = "userconnections", key = "#userId")
    public UserConnections findByUser(String userId) {

        Criteria criteria = Criteria.where("user.$id").is(new ObjectId(userId));
        Query query = new Query(criteria);

        UserConnections user = op.findOne(query, domainClass);
        return user;
    }

    public boolean contains(UserEntity entity, Location location) {

        UserConnections conn = findByUser(entity.getId());
        int pos = contains(conn, location);

        return pos < 0 ? false : true;
    }

    public boolean containsRemoved(UserEntity entity, Location location) {

        UserConnections conn = findByUser(entity.getId());
        int pos = containsRemoved(conn, location);

        return pos < 0 ? false : true;
    }

    @CacheEvict(value = "userconnections", allEntries = true)
    public void removeLocationFromUserConnections(Location location, UserEntity user) {

        UserConnections conn = findByUser(user.getId());
        if( conn == null )
            return;

        removeLocation(location, conn);
    }

    @CacheEvict(value = "userconnections", allEntries = true)
    public void restoreLocationToUserConnections(Location location, UserEntity user) {

        UserConnections conn = findByUser(user.getId());
        if( conn == null )
            return;

        addLocation(location, conn);
    }

    @CacheEvict(value = "userconnections", allEntries = true)
    public void removeLocationFromConnections(Location location) {

        List<UserConnections> list = this.findAll();
        for (UserConnections conn : list) {
            removeLocation(location, conn);
        }

    }

    private void addLocation(Location location, UserConnections conn) {

        int pos = contains(conn, location);
        if( pos >= 0 )
            return;

        List<String> locations = conn.getLocations();
        if( locations == null ) {
            locations = new ArrayList<String>();
            conn.setLocations(locations);
        }

        locations.add(location.getId());

        int rem = containsRemoved(conn, location);
        if( rem >= 0 ) {
            conn.getRemovedLocations().remove(rem);
        }

        op.save(conn);
    }

    private void removeLocation(Location location, UserConnections conn) {

        int pos = contains(conn, location);
        if( pos < 0 )
            return;

        conn.getLocations().remove(pos);

        int rem = containsRemoved(conn, location);
        if( rem < 0 ) {
            List<String> removed = conn.getRemovedLocations();
            if( removed == null ) {
                removed = new ArrayList<String>();
                conn.setRemovedLocations(removed);
            }
            removed.add(location.getId());
        }

        op.save(conn);
    }

    public int contains(UserConnections conn, Location location) {

        if( conn == null )
            return -1;

        if(location == null)
            return -1;

        List<String> locations = conn.getLocations();
        if( locations == null )
            return -1;

        int pos = -1;
        boolean ok = false;
        for(pos = 0; pos < locations.size(); pos++) {
            String s = locations.get(pos);
            if( s.equals(location.getId())) {
                ok = true;
                break;
            }
        }

        if( ok )
            return pos;
        return -1;
    }

    public int containsRemoved(UserConnections conn, Location location) {

        if( location == null )
            return -1;

        return containsRemoved(conn, location.getId());
    }

    public int containsRemoved(UserConnections conn, String locationId) {
        if( conn == null )
            return -1;

        if( locationId == null )
            return -1;

        List<String> removed = conn.getRemovedLocations();
        if( removed == null )
            return -1;

        int pos = -1;
        boolean ok = false;
        for(pos = 0; pos < removed.size(); pos++) {
            String s = removed.get(pos);
            if( s.equals(locationId)) {
                ok = true;
                break;
            }
        }

        if( ok )
            return pos;
        return -1;
    }
}
