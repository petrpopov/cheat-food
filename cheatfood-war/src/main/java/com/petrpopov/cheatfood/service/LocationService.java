package com.petrpopov.cheatfood.service;

import com.petrpopov.cheatfood.config.CheatException;
import com.petrpopov.cheatfood.model.*;
import org.apache.log4j.Logger;
import org.bson.types.ObjectId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.IndexOperations;
import org.springframework.data.mongodb.core.geo.Box;
import org.springframework.data.mongodb.core.index.GeospatialIndex;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

/**
 * User: petrpopov
 * Date: 02.07.13
 * Time: 17:51
 */

@Component
public class LocationService extends GenericService<Location>{

    @Autowired
    private TypeService typeService;

    private Logger logger = Logger.getLogger(LocationService.class);

    public LocationService() {
        super(Location.class);
    }

    @PostConstruct
    public void init() {
        GeospatialIndex index = new GeospatialIndex("geoLocation");
        IndexOperations indexOperations = op.indexOps(Location.class);
        indexOperations.ensureIndex(index);
    }

    public List<Location> findAllInBounds(@Valid GeoPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query(Criteria.where("geoLocation").within(box));

        return op.find(query, Location.class);
    }

    public List<Location> findAllTypeInBounds(@Valid GeoPointBounds bounds, String typeId) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query();

        Criteria criteria = new Criteria();
        criteria.andOperator(Criteria.where("geoLocation").within(box), Criteria.where("type.$id").is(new ObjectId(typeId)));
        query.addCriteria(criteria);

        List<Location> locations = op.find(query, Location.class);
        return locations;
    }

    public List<Location> findAllInDifference(@Valid GeoPointBounds inBounds, GeoPointBounds notInBounds, String typeId) {

        if( notInBounds == null ) {
            return this.findAllInBounds(inBounds);
        }

        if( notInBounds.equals(inBounds) ) {
            return this.findAllInBounds(inBounds);
        }

        Type type = null;
        if( typeId != null ) {
            if( !typeId.isEmpty() ) {
                type = typeService.findById(typeId);
            }
        }

        //TODO: very shitty code !
        List<Location> inList = type == null ? this.findAllInBounds(inBounds) : this.findAllTypeInBounds(inBounds, typeId);
        List<Location> notInList = type == null ? this.findAllInBounds(notInBounds) :this.findAllTypeInBounds(notInBounds, typeId);

        if( notInList.equals(inList) ) {
            return inList;
        }

        List<Location> res = new ArrayList<Location>();
        for (Location location : inList) {
            if( !notInList.contains(location) )
                res.add(location);
        }

        return res;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public Location createOrSave(@Valid Location location, UserEntity userEntity) {

        Type savedType = getTypeForLocation(location);
        if( savedType != null ) {
            logger.info("Setting saved Type to location");
            location.setType(savedType);
        }

        location.setCreator(userEntity);

        location.setVotes(this.getLocationVotes(location));

        logger.info("Saving location to database");

        return saveLocationObject(location);
    }

    @PreAuthorize("hasRole('ROLE_USER') and #location.creator.id==principal.username")
    public void deleteLocation(Location location) {
        logger.info("Deleting location from database by object");
        op.remove(location);
    }

    public long getLocationsCountInBound(@Valid GeoPointBounds bounds) {

        Box box = this.getBoxFromBounds(bounds);
        Query query = new Query(Criteria.where("geoLocation").within(box) );

        long count = op.count(query, Location.class);
        return count;
    }

    @PreAuthorize("hasRole('ROLE_USER')")
    public void voteForLocation(Location location, Vote vote) throws CheatException {

        List<Vote> votes = location.getVotes();
        if( votes != null ) {
            for (Vote vote1 : votes) {
                if( vote1.getUserId().equals(vote.getUserId()))
                    throw new CheatException(ErrorType.already_voted);
            }
        }
        else {
            votes = new ArrayList<Vote>();
            location.setVotes(votes);
        }

        votes.add(vote);
        saveLocationObject(location);
    }

    private List<Vote> getLocationVotes(Location location) {
        Location location1 = this.findById(location.getId());
        if( location1 == null )
            return null;

        return location1.getVotes();
    }

    private Location saveLocationObject(Location location) {
        if( location.getId() != null ) {
            if( location.getId().isEmpty() ) {
                location.setId(null);
            }
        }
        op.save(location);
        return location;
    }

    private Type getTypeForLocation(Location location) {

        Type type = location.getType();
        if( type == null ) {
            return null;
        }

        String typeId = type.getId();
        if( typeId == null ) {
            return null;
        }
        else if( typeId.isEmpty() ) {
            return null;
        }

        Type savedType = typeService.findById(typeId);
        return savedType;
    }

    private Box getBoxFromBounds(GeoPointBounds bounds) {
        Geo2DPoint northEast = bounds.getNorthEast2DPoint();
        Geo2DPoint southWest = bounds.getSouthWest2DPoint();

        Box box = new Box(southWest.getCoordinates(), northEast.getCoordinates());

        return box;
    }
}
