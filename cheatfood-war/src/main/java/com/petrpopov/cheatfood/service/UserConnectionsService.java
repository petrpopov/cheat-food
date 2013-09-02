package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.model.entity.Location;
import com.petrpopov.cheatfood.model.entity.UserConnections;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Component;

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

    public UserConnections findByUser(String userId) {

        Criteria criteria = Criteria.where("user.$id").is(new ObjectId(userId));
        Query query = new Query(criteria);

        UserConnections user = op.findOne(query, domainClass);
        return user;
    }

    public void removeLocationFromConnections(Location location) {

        List<UserConnections> list = this.findAll();
        for (UserConnections conn : list) {

            int pos = contains(conn, location);
            if( pos < 0 )
                continue;

            conn.getLocations().remove(pos);
            op.save(conn);
        }

    }

    private int contains(UserConnections conn, Location location) {

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
}
